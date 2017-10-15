package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.UsageStock_;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
    private StockService stockService;
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
    public Specification<MainOrder> search(String orderId, String mobile, Long goodId, LocalDate orderDate
            , LocalDate beginDate, LocalDate endDate, OrderStatus status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (!StringUtils.isEmpty(orderId)) {
                log.debug("search order with orderId:" + orderId);
                //前面8位是 时间
                predicate = cb.and(predicate, orderIdPredicate(orderId, root, cb));
            } else if (orderDate != null) {
                log.debug("search order with orderDate:" + orderDate);
                predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get(MainOrder_.orderTime), orderDate.toString()));
            } else {
                // 日期过滤
                if (beginDate != null) {
                    predicate = cb.and(predicate, JpaFunctionUtils.ymd(cb, root.get(MainOrder_.orderTime), beginDate
                            , CriteriaBuilder::greaterThanOrEqualTo));
                }
                if (endDate != null)
                    predicate = cb.and(predicate, JpaFunctionUtils.ymd(cb, root.get(MainOrder_.orderTime), endDate
                            , CriteriaBuilder::lessThanOrEqualTo));
            }
            if (!StringUtils.isEmpty(mobile)) {
                log.debug("search order with mobile:" + mobile);
                // 2个都可以
                predicate = cb.and(predicate, cb.like(Customer.getMobile(MainOrder.getCustomer(root)), "%" + mobile + "%"));
            }
            if (goodId != null) {
                root.fetch(MainOrder_.amounts);
                predicate = cb.and(predicate, cb.equal(root.join(MainOrder_.amounts).key().get(MainGood_.id), goodId));
            }
            if (status != null && status != OrderStatus.EMPTY) {
                if (status == OrderStatus.forDeliver) {
                    predicate = cb.and(predicate, cb.or(
                            cb.equal(root.get(MainOrder_.orderStatus), status)
                            , cb.and(
                                    cb.equal(root.get(MainOrder_.orderStatus), OrderStatus.forDeliverConfirm)
                                    , cb.equal(root.get(MainOrder_.ableShip), true)
                            )
                    ));
                } else
                    predicate = cb.and(predicate, cb.equal(root.get(MainOrder_.orderStatus), status));
            }

            return predicate;
        };
    }

    private Predicate orderIdPredicate(String orderId, Root<MainOrder> root, CriteriaBuilder cb) {
        String ymd = orderId.substring(0, 8);
        return cb.and(
                cb.equal(root.get("dailySerialId"), NumberUtils.parseNumber(orderId.substring(8), Integer.class))
                , JpaFunctionUtils.dateEqual(cb, root.get("orderTime")
                        , LocalDate.from(MainOrder.SerialDateTimeFormatter.parse(ymd)).toString())
        );
    }

    @Override
    public Specification<MainOrder> search(String search, OrderStatus status) {
        if (StringUtils.isEmpty(search) && (status == null || status == OrderStatus.EMPTY))
            return null;
        return (root, query, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (!StringUtils.isEmpty(search)) {
                log.debug("search order with mobile:" + search);
                // 2个都可以
                predicate = cb.and(predicate, cb.like(Customer.getMobile(MainOrder.getCustomer(root)), "%" + search + "%"));
            }

            if (status != null && status != OrderStatus.EMPTY) {
                predicate = cb.and(predicate, cb.equal(root.get("orderStatus"), status));
            }

            return predicate;
        };
    }

    @Override
    public void updateOrderTime(LocalDateTime time) {
        CriteriaUpdate<MainOrder> criteriaUpdate = entityManager.getCriteriaBuilder().createCriteriaUpdate(MainOrder.class);
        Root<MainOrder> root = criteriaUpdate.from(MainOrder.class);
        criteriaUpdate = criteriaUpdate.set(root.get("orderTime"), time);
        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public List<Depot> depotsForOrder(long orderId) {
        MainOrder order = getOrder(orderId);
        // 库存多的优先
        return stockService.usableDepotFor((cb, root)
                -> cb.and(
                order.getAmounts().entrySet().stream()
                        .map(entry -> cb.and(
                                cb.equal(root.get(UsageStock_.product), entry.getKey().getProduct())
                                , cb.greaterThanOrEqualTo(root.get(UsageStock_.amount), entry.getValue())
                        ))
                        .toArray(Predicate[]::new)
        ));
//        final MainProduct product = order.getGood().getProduct();
//        return stockService.enabledUsableStockInfo(((productPath, criteriaBuilder)
//                -> criteriaBuilder.equal(productPath, product)), null)
//                .forProduct(product);
    }


    @Override
    public MainOrderFinishEvent forOrderInstalledEvent(OrderInstalledEvent event) {
        if (event.getOrder() instanceof MainOrder) {
            return new MainOrderFinishEvent((MainOrder) event.getOrder(), event.getSource());
        }
        return null;
    }

    @Override
    public DeliverableOrder orderFor(StockShiftUnit unit) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MainOrder> cq = cb.createQuery(MainOrder.class);
        Root<MainOrder> root = cq.from(MainOrder.class);
        try {
            return entityManager.createQuery(cq
                    .where(cb.isMember(unit, root.get("logisticsSet")))
            )
                    .getSingleResult();
        } catch (NoResultException ignored) {
            log.error("居然没有这个订单！我们还做别的生意么?" + unit.getId(), ignored);
            return null;
        }
    }

    @Override
    public List<MainOrder> byOrderBy(Login login) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MainOrder> cq = cb.createQuery(MainOrder.class);
        Root<MainOrder> root = cq.from(MainOrder.class);
        return entityManager.createQuery(cq.where(cb.equal(root.get(MainOrder_.orderBy), login)))
                .getResultList();
    }

    @Getter
    @AllArgsConstructor
    class OffsetStock {
        //清算日期
        private LocalDate offsetDate;
        private Integer stock;
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
