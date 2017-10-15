package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CJ
 */
@Service
public class MainOrderServiceImpl extends AbstractMainDeliverableOrderService<MainOrder> implements MainOrderService {

    private static final Log log = LogFactory.getLog(MainOrderServiceImpl.class);
    private static final int defaultMaxMinuteForPay = 60 * 24 * 3;
    //用于关闭超时未支付订单
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);
    @Autowired
    private LoginService loginService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    /**
     * 保存每日序列号的
     */
    private Map<LocalDate, AtomicInteger> dailySerials = Collections.synchronizedMap(new HashMap<>());
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private Environment env;

    @PreDestroy
    public void beforeClose() {
        executor.shutdown();
    }

    @Override
    protected MainOrder newOrder(Login who, Login recommendBy) {
        MainOrder order = new MainOrder();
        order.setOrderBy(who);
        order.setRecommendBy(recommendBy);
        return order;
    }

    @Override
    protected MainOrder persistOrder(MainOrder order, String mortgageIdentifier) {
        order.setMortgageIdentifier(mortgageIdentifier);
        queryDailySerialId(order.getOrderTime().toLocalDate(), order);

        order.setOrderStatus(OrderStatus.forPay);
        order = mainOrderRepository.saveAndFlush(order);
        //单元测试默认为空
        Integer maxMinuteForPay = systemStringService.getCustomSystemString(
                "market.core.service.order.maxMinuteForPay", null, true, Integer.class
                , !env.acceptsProfiles(CoreConfig.ProfileUnitTest) ? defaultMaxMinuteForPay : null);
        //如果开启了 关闭订单 这个功能
        if (maxMinuteForPay != null) {
            //创建成功，建立 Executor 在指定时间内关闭订单
            //如果是跑单元测试，就把单位设置为秒
            executor.schedule(new OrderPayStatusCheckThread(order.getId()), maxMinuteForPay
                    , !env.acceptsProfiles(CoreConfig.ProfileUnitTest) ? TimeUnit.MINUTES : TimeUnit.SECONDS);
        }
        return order;
    }

    @Override
    public MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, Map<MainGood, Integer> amounts, String mortgageIdentifier) throws MainGoodLowStockException {
        //这里通过外部来调用这个方法是防止跳过AOP
        return applicationContext.getBean(MainOrderService.class).newOrder(who, recommendBy, name, mobile, age, gender
                , installAddress, new Amounts(amounts), mortgageIdentifier);
    }

    @Override
    public void createExecutorToForPayOrder() {
        List<MainOrder> forPayOrderList = mainOrderRepository.findAll(search(null, OrderStatus.forPay));
        log.info("存在" + forPayOrderList.size() + "个未支付订单");
        Integer maxMinuteForPay = systemStringService.getCustomSystemString("market.core.service.order.maxMinuteForPay", null, true, Integer.class, defaultMaxMinuteForPay);
        //如果需要 关闭订单 这个功能
        if (maxMinuteForPay != null) {
            LocalDateTime now = LocalDateTime.now();
            //已经超过关闭时间的，直接把订单关掉
            long count;
            if (!env.acceptsProfiles(CoreConfig.ProfileUnitTest)) {
                count = forPayOrderList.stream().filter(order -> !order.getOrderTime().plusMinutes(maxMinuteForPay).isAfter(now)).count();
                log.info("即将关闭" + count + "个订单");
                forPayOrderList.stream().filter(order -> !order.getOrderTime().plusMinutes(maxMinuteForPay).isAfter(now))
                        .forEach(order -> order.setOrderStatus(OrderStatus.close));

                //还没超过时间的，定义ExecutorService
                forPayOrderList.stream().filter(order -> order.getOrderTime().plusMinutes(maxMinuteForPay).isAfter(now))
                        .forEach(order -> {
                            long waitMinute = ChronoUnit.MINUTES.between(now, order.getOrderTime().plusMinutes(maxMinuteForPay));
                            executor.schedule(new OrderPayStatusCheckThread(order.getId()), waitMinute, TimeUnit.MINUTES);
                        });
            } else {
                count = forPayOrderList.stream().filter(order -> !order.getOrderTime().plusSeconds(maxMinuteForPay).isAfter(now)).count();
                log.info("即将关闭" + count + "个订单");
                forPayOrderList.stream().filter(order -> !order.getOrderTime().plusSeconds(maxMinuteForPay).isAfter(now))
                        .forEach(order -> order.setOrderStatus(OrderStatus.close));

                //还没超过时间的，定义ExecutorService
                forPayOrderList.stream().filter(order -> order.getOrderTime().plusSeconds(maxMinuteForPay).isAfter(now))
                        .forEach(order -> {
                            long waitMinute = ChronoUnit.MINUTES.between(now, order.getOrderTime().plusMinutes(maxMinuteForPay));
                            log.info("wait time:" + waitMinute);
                            executor.schedule(new OrderPayStatusCheckThread(order.getId()), waitMinute, TimeUnit.SECONDS);
                        });
            }
        }
    }

    private synchronized void queryDailySerialId(LocalDate now, MainOrder order) {
        if (!dailySerials.containsKey(now)) {
            // 寻找当前库最大值
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Integer> max = criteriaBuilder.createQuery(Integer.class);
            Root<MainOrder> root = max.from(MainOrder.class);
            max = max.where(JpaFunctionUtils.dateEqual(criteriaBuilder, root.get("orderTime")
                    , now.toString()));
            max = max.select(criteriaBuilder.max(root.get("dailySerialId")));
            try {
                dailySerials.put(now, new AtomicInteger(entityManager.createQuery(max).getSingleResult()));
            } catch (Exception ignored) {
//                log.trace("", ignored);
                dailySerials.put(now, new AtomicInteger(0));
            }
        }

        order.setDailySerialId(dailySerials.get(now).incrementAndGet());
    }

    @Override
    public List<MainOrder> allOrders() {
        return mainOrderRepository.findAll();
    }

    @Override
    public MainOrder getOrder(long id) {
        return mainOrderRepository.getOne(id);
    }

    @Override
    public MainOrder getOrder(String orderId) {
        MainOrder order = mainOrderRepository.findOne((root, query, cb) -> orderIdPredicate(orderId, root, cb));
        if (order == null)
            throw new EntityNotFoundException();
        return order;
    }

    @Override
    public boolean isPaySuccess(long id) {
        return mainOrderRepository.getOne(id).isPay();
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Boolean> criteriaQuery = criteriaBuilder.createQuery(Boolean.class);
//        Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
//        criteriaQuery = criteriaQuery.select(root.get("pay"));
//        criteriaQuery = criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
//        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public Login getEnjoyability(MainOrder order) {
        Login orderBy = order.getOrderBy();
        return getEnjoyability(orderBy);
    }

    @Override
    public Login getEnjoyability(Login orderBy) {
        Login login = orderBy;
        while (!loginService.isRegularLogin(login)) {
            // 最终都没有找到收益人 则给 管理员。。
            if (login == null)
                return loginService.byLoginName("master");
            login = login.getGuideUser();
        }
        return login;
    }

    @Override
    public boolean supportOrderId() {
        return true;
    }

    @Override
    public Predicate orderIdPredicate(String orderId, Root<MainOrder> root, CriteriaBuilder cb) {
        String ymd = orderId.substring(0, 8);
        return cb.and(
                cb.equal(root.get("dailySerialId"), NumberUtils.parseNumber(orderId.substring(8), Integer.class))
                , JpaFunctionUtils.dateEqual(cb, root.get("orderTime")
                        , LocalDate.from(MainOrder.SerialDateTimeFormatter.parse(ymd)).toString())
        );
    }

    @Override
    public void updateOrderTime(LocalDateTime time) {
        CriteriaUpdate<MainOrder> criteriaUpdate = entityManager.getCriteriaBuilder().createCriteriaUpdate(MainOrder.class);
        Root<MainOrder> root = criteriaUpdate.from(MainOrder.class);
        criteriaUpdate = criteriaUpdate.set(root.get("orderTime"), time);
        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public MainOrderFinishEvent forOrderInstalledEvent(OrderInstalledEvent event) {
        if (event.getOrder() instanceof MainOrder) {
            return new MainOrderFinishEvent((MainOrder) event.getOrder(), event.getSource());
        }
        return null;
    }

    @Override
    public Class<MainOrder> getOrderClass() {
        return MainOrder.class;
    }

    @Override
    public List<MainOrder> byOrderBy(Login login) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MainOrder> cq = cb.createQuery(MainOrder.class);
        Root<MainOrder> root = cq.from(MainOrder.class);
        return entityManager.createQuery(cq.where(cb.equal(root.get(MainOrder_.orderBy), login)))
                .getResultList();
    }

    /**
     * 用于关闭订单
     */
    class OrderPayStatusCheckThread implements Runnable {
        private Long orderId;

        OrderPayStatusCheckThread(Long orderId) {
            this.orderId = orderId;
        }

        @Override
//        @Transactional 不会有任何作用的
        public void run() {
            MainOrder order = mainOrderRepository.findOne(orderId);
            if (order.getOrderStatus() == OrderStatus.forPay) {
                //还没付款，就关闭订单
                order.setOrderStatus(OrderStatus.close);
                mainOrderRepository.save(order);
            }
        }
    }
}
