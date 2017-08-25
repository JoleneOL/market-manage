package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.*;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderDeliveredEvent;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.exception.MainGoodLimitStockException;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.aop.MultiBusinessSafe;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.UsageStock_;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class MainOrderServiceImpl implements MainOrderService {

    private static final Log log = LogFactory.getLog(MainOrderServiceImpl.class);

    @Autowired
    private CustomerService customerService;
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
    private HaierSupplier haierSupplier;
    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private Environment env;

    private Map<String, Integer> productStockMap = new HashMap<>();
    private static final int defaultMaxMinuteForPay = 60*24*3;
    private static final int defaultOffsetHour = 9;

    @PostConstruct
    @Transactional
    public void initExecutor() {
        createExecutorToForPayOrder();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (true){
                //获取限购清算时间
                log.info("清除货品限购");
                productStockMap.clear();
                int offsetHour = systemStringService.getCustomSystemString("market.core.service.product.offsetHour", null, true, Integer.class, defaultOffsetHour);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime nextRuntime;
                if(now.getHour() >= offsetHour){
                    nextRuntime = LocalDate.now().plusDays(1).atTime(offsetHour,0);
                }else{
                    nextRuntime = LocalDate.now().atTime(offsetHour,0);
                }
                long sleepTime = ChronoUnit.MILLIS.between(now,nextRuntime);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    @Override
    public MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, Amounts amounts, String mortgageIdentifier) throws MainGoodLowStockException {
        // 客户处理
        Customer customer = customerService.getNoNullCustomer(name, mobile, loginService.lowestAgentLevel(getEnjoyability(who))
                , recommendBy);
        //检查货品库存数量
        checkGoodStock(amounts.getAmounts());

        customer.setInstallAddress(installAddress);
        customer.setGender(gender);
        final LocalDate now = LocalDate.now();
        customer.setBirthYear(now.getYear() - age);

        MainOrder order = new MainOrder();
//        order.setAmount(amount);
        order.setAmounts(amounts.getAmounts());
        order.setCustomer(customer);
        order.setInstallAddress(installAddress);
        order.setMortgageIdentifier(mortgageIdentifier);
        order.setOrderBy(who);
        order.setRecommendBy(recommendBy);
        order.setOrderTime(LocalDateTime.now());
//        order.setGood(good);
        order.makeRecord();

        queryDailySerialId(now, order);
        order.setOrderStatus(OrderStatus.forPay);
        order = mainOrderRepository.saveAndFlush(order);
        Integer maxMinuteForPay = systemStringService.getCustomSystemString(
                "market.core.service.order.maxMinuteForPay", null, true, Integer.class
                , defaultMaxMinuteForPay);
        //如果开启了 关闭订单 这个功能
        if (maxMinuteForPay != null) {
            //创建成功，建立 Executor 在指定时间内关闭订单
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            //如果是跑单元测试，就把单位设置为秒
            executor.scheduleAtFixedRate(new OrderPayStatusCheckThread(order.getId(), executor), maxMinuteForPay
                    , maxMinuteForPay
                    , !env.acceptsProfiles(CoreConfig.ProfileUnitTest) ? TimeUnit.MINUTES : TimeUnit.SECONDS);
        }
        return order;
    }

    @Override
    public MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, Map<MainGood, Integer> amounts, String mortgageIdentifier) throws MainGoodLowStockException {
        //这里通过外部来调用这个方法是防止跳过AOP
        return applicationContext.getBean(MainOrderService.class).newOrder(who,recommendBy,name,mobile,age,gender
                ,installAddress,new Amounts(amounts),mortgageIdentifier);
    }

    @Override
    public void createExecutorToForPayOrder() {
        List<MainOrder> forPayOrderList = mainOrderRepository.findAll(search(null, OrderStatus.forPay));
        Integer maxMinuteForPay = systemStringService.getCustomSystemString("market.core.service.order.maxMinuteForPay", null, true, Integer.class, defaultMaxMinuteForPay);
        //如果需要 关闭订单 这个功能
        if (maxMinuteForPay != null) {
            LocalDateTime now = LocalDateTime.now();
            //已经超过关闭时间的，直接把订单关掉
            forPayOrderList.stream().filter(order -> !order.getOrderTime().plusMinutes(maxMinuteForPay).isBefore(now))
                    .forEach(order -> order.setOrderStatus(OrderStatus.close));
            //还没超过时间的，定义ExecutorService
            forPayOrderList.stream().filter(order -> order.getOrderTime().plusMinutes(maxMinuteForPay).isAfter(now))
                    .forEach(order -> {
                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        long waitMinute = ChronoUnit.MINUTES.between(now, order.getOrderTime().plusMinutes(maxMinuteForPay));
                        executor.scheduleWithFixedDelay(new OrderPayStatusCheckThread(order.getId(), executor), waitMinute, waitMinute, TimeUnit.MINUTES);
                    });
        }
    }

    @Override
    public void cleanProductStock(Product product) {
        if (productStockMap.containsKey(product.getCode())) {
            productStockMap.remove(product.getCode());
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
                return loginService.byLoginName("root");
            login = login.getGuideUser();
        }
        return login;
    }

    @Override
    public Specification<MainOrder> search(String orderId, String mobile, Long goodId, LocalDate orderDate
            , OrderStatus status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (!StringUtils.isEmpty(orderId)) {
                log.debug("search order with orderId:" + orderId);
                //前面8位是 时间
                predicate = cb.and(predicate, orderIdPredicate(orderId, root, cb));
            } else if (orderDate != null) {
                log.debug("search order with orderDate:" + orderDate);
                predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get("orderTime"), orderDate.toString()));
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
                predicate = cb.and(predicate, cb.equal(root.get("orderStatus"), status));
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
    public StockShiftUnit makeLogistics(Class<? extends LogisticsSupplier> supplierType, long orderId, long depotId) {
        MainOrder order = getOrder(orderId);
        Depot depot = depotRepository.getOne(depotId);

        LogisticsSupplier supplier;
        if (supplierType == HaierSupplier.class)
            supplier = haierSupplier;
        else
            supplier = applicationContext.getBean(supplierType);

        StockShiftUnit unit = logisticsService.makeShift(supplier, order.getAmounts().entrySet().stream()
                        .map((Function<Map.Entry<MainGood, Integer>, Thing>) entry -> new Thing() {
                            @Override
                            public Product getProduct() {
                                return entry.getKey().getProduct();
                            }

                            @Override
                            public ProductStatus getProductStatus() {
                                return ProductStatus.normal;
                            }

                            @Override
                            public int getAmount() {
                                return entry.getValue();
                            }
                        })
                        .collect(Collectors.toSet())
                , depot, order, LogisticsOptions.Installation);

        if (order.getLogisticsSet() == null)
            order.setLogisticsSet(new ArrayList<>());

        order.getLogisticsSet().add(unit);
        order.setCurrentLogistics(unit);
        order.setOrderStatus(OrderStatus.forDeliverConfirm);
        return unit;
    }

    @Override
    public void forInstallationEvent(InstallationEvent event) {
        logisticsToMainOrder(event.getUnit(), order -> {
            final OrderStatus currentOrderStatus = order.getOrderStatus();
            order.setOrderStatus(OrderStatus.afterSale);
            if (currentOrderStatus != OrderStatus.forInstall) {
                log.error(order.getSerialId() + "尚未收货就安装完成了。");
            }
            applicationEventPublisher.publishEvent(new MainOrderFinishEvent(order, event));
        });
    }

    @Override
    public int sumProductNum(Product product) {
        return sumProductNum(product, null, null, OrderStatus.forPay, OrderStatus.forDeliver);
    }

    @Override
    public int sumProductNum(Product product, LocalDateTime beginTime, LocalDateTime endTime, OrderStatus... orderStatuses) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MainOrder> root = cq.from(MainOrder.class);
        //今日核算时间之前的订单
        MapJoin<MainOrder, MainGood, Integer> amountsRoot = root.join(MainOrder_.amounts);
        List<Predicate> list = new ArrayList<>();
        list.add(cb.notEqual(root.get(MainOrder_.orderStatus), OrderStatus.close));
        list.add(cb.equal(amountsRoot.key().get(MainGood_.product), product));
        if (orderStatuses != null) {
            list.add(root.get(MainOrder_.orderStatus)
                    .in(orderStatuses));
        }
        if (beginTime != null) {
            list.add(cb.greaterThanOrEqualTo(root.get(MainOrder_.orderTime), beginTime));
        }
        if (endTime != null) {
            list.add(cb.lessThanOrEqualTo(root.get(MainOrder_.orderTime), endTime));
        }
        Predicate[] p = new Predicate[list.size()];
        cq.where(cb.and(list.toArray(p)));
        cq.select(cb.sum(amountsRoot.value()));
        Object result = entityManager.createQuery(cq).getSingleResult();
        return result != null ? (int) result : 0;
    }

    @Override
    public int limitStock(Product product) {
        //如果已经计算过了，就直接从 map 中获取
        if (productStockMap.containsKey(product.getCode())) {
            return productStockMap.get(product.getCode());
        }
        long limitDay;
        LocalDateTime now = LocalDateTime.now();
        //如果未设置限购时间，或者限购时间已经超过了，那么货品就不限购
        if (product instanceof MainProduct) {
            LocalDate planSellOutDate = ((MainProduct) product).getPlanSellOutDate();
            if (planSellOutDate == null || planSellOutDate.isBefore(now.toLocalDate())) {
                limitDay = 1L;
            } else {
                int offsetHour = systemStringService.getCustomSystemString("market.core.service.product.offsetHour", null, true, Integer.class, defaultOffsetHour);
                limitDay = ChronoUnit.DAYS.between(now.minusHours(offsetHour).toLocalDate(), planSellOutDate) + 1;
            }
        } else {
            limitDay = 1L;

        }
        int totalUsableStock = stockService.usableStockTotal(product);
        //锁定库存包括 代付款，待发货
        int lockedStock = sumProductNum(product);
        //(UsageStock - sumProductNum) / N，这里计算的不算精确，没有考虑到今日冻结的库存数，但是在可接受范围内
        int productStock = lockedStock > totalUsableStock ? 0 : (int) ((totalUsableStock - lockedStock) / limitDay);
        productStockMap.put(product.getCode(), productStock);
        return productStock;
    }

    @Override
    public int usableStock(Product product) {
        int limitStock = limitStock(product);
        int offsetHour = systemStringService.getCustomSystemString("market.core.service.product.offsetHour", null, true, Integer.class, defaultOffsetHour);
        LocalDateTime orderBeginTime = LocalDateTime.now().withHour(offsetHour);
        //计算今日所有未关闭订单的货品数量
        int todayStock = sumProductNum(product, orderBeginTime, null);
        return todayStock > limitStock ? 0 : limitStock - todayStock;
    }

    @Override
    public void calculateGoodStock(Collection<MainGood> mainGoodSet) {
        mainGoodSet.forEach(mainGood -> mainGood.getProduct().setStock(usableStock(mainGood.getProduct())));
    }

    private void checkGoodStock(Map<MainGood, Integer> amounts) throws MainGoodLowStockException {
        for (MainGood good : amounts.keySet()) {
            int usableStock = usableStock(good.getProduct());
            if (good.getProduct().getPlanSellOutDate() == null && usableStock < amounts.get(good)) {
                throw new MainGoodLowStockException(good);
            } else if (good.getProduct().getPlanSellOutDate() != null && usableStock < amounts.get(good)) {
                int offsetHour = systemStringService.getCustomSystemString("market.core.service.product.offsetHour", null, true, Integer.class, defaultMaxMinuteForPay);
                LocalDateTime localDateTime = LocalDate.now().plusDays(1).atStartOfDay().plusHours(offsetHour);
                throw new MainGoodLimitStockException(good, localDateTime);
            }
        }
    }

    @Override
    public void forShiftEvent(ShiftEvent event) {
        // 基于物流的变化，需要对订单进行状态更新
        // 只关注 拒绝事件
        final ShiftStatus toStatus = event.getStatus();
        if (toStatus != ShiftStatus.reject
                && toStatus != ShiftStatus.success)
            return;
        logisticsToMainOrder(event.getUnit(), order -> {
            final OrderStatus currentOrderStatus = order.getOrderStatus();
            switch (toStatus) {
                case reject:
                    if (currentOrderStatus == OrderStatus.forDeliverConfirm
                            || currentOrderStatus == OrderStatus.forInstall
                            || currentOrderStatus == OrderStatus.afterSale
                            )
                        order.setOrderStatus(OrderStatus.forDeliver);
                    else {
                        log.error("错误逻辑，应该是未进入物流状态的订单 收到了物流失败的事件。" + order.getSerialId());
                    }
                    break;
                case success:
                    if (currentOrderStatus == OrderStatus.forDeliverConfirm)
                        order.setOrderStatus(OrderStatus.forInstall);
                    applicationEventPublisher.publishEvent(new MainOrderDeliveredEvent(order, event));
                    break;
                default:
            }
        });

    }

    private void logisticsToMainOrder(final StockShiftUnit unit, Consumer<MainOrder> consumer) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MainOrder> cq = cb.createQuery(MainOrder.class);
        Root<MainOrder> root = cq.from(MainOrder.class);
        try {
            MainOrder order = entityManager.createQuery(cq
                    .where(cb.isMember(unit, root.get("logisticsSet")))
            )
                    .getSingleResult();
            consumer.accept(order);
        } catch (NoResultException ignored) {
            log.error("居然没有这个订单！我们还做别的生意么?" + unit.getId(), ignored);
        }
    }

    /**
     * 用于关闭订单
     */
    class OrderPayStatusCheckThread implements Runnable {
        private Long orderId;
        private ExecutorService executor;

        OrderPayStatusCheckThread(Long orderId, ExecutorService executor) {
            this.orderId = orderId;
            this.executor = executor;
        }

        @Override
        @Transactional
        public void run() {
            MainOrder order = mainOrderRepository.findOne(orderId);
            if (order.getOrderStatus() == OrderStatus.forPay) {
                //还没付款，就关闭订单
                order.setOrderStatus(OrderStatus.close);
                mainOrderRepository.save(order);
            }
            executor.shutdown();
        }
    }
}
