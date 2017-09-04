package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.record.MainOrderRecord;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.util.CommissionSource;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.thread.ThreadLocker;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainOrder implements PayableOrder, CommissionSource, ThreadLocker, LogisticsDestination {
    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);
    /**
     * 最长长度
     */
    private static final int MaxDailySerialIdBit = 6;
    private static final Log log = LogFactory.getLog(MainOrder.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 每日序列号
     * 业务量每天不会超过1000000
     * 所以这个号在整体订单号中长度为 6
     */
    private int dailySerialId;
    /**
     * 原始记录
     */
    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private MainOrderRecord record;
    /**
     * 谁下的单，并不意味着它即可获得收益，需要确保该用户是否是正式用户（即下过一单或者是一个代理商，如果不是则给他的引导者）
     */
    @ManyToOne
    private Login orderBy;
    /**
     * 谁买的
     */
    @ManyToOne
    private Customer customer;
    /**
     * 谁推荐的
     */
    @ManyToOne
    private Login recommendBy;
    private Address installAddress;
    /**
     * 具体的产品
     */
    @ManyToOne
    @Deprecated
    private MainGood good;
    @Deprecated
    private int amount;
    /**
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @ElementCollection
    private Map<MainGood, Integer> amounts;
    /**
     * 按揭识别码
     */
    @Column(length = 32)
    private String mortgageIdentifier;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime orderTime;
    /**
     * 成功支付的支付订单
     */
    @ManyToOne
    private PayOrder payOrder;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime payTime;
    /**
     * 订单状态
     */
    private OrderStatus orderStatus;
    /**
     * 下单时的总价
     * 将在{@link #makeRecord()}时被记录
     */
    @Deprecated
    @Column(scale = 2, precision = 12)
    private BigDecimal goodTotalPrice;
    /**
     * 下单时用于结算佣金的价格
     * 将在{@link #makeRecord()}时被记录
     */
    @Deprecated
    @Column(scale = 2, precision = 12)
    private BigDecimal goodCommissioningPrice;
    /**
     * 下单时的总价，该总价不依赖于数量即已经被完整计算
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal goodTotalPriceAmountIndependent;
    /**
     * 下单时用于结算佣金的价格
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal goodCommissioningPriceAmountIndependent;
    /**
     * 下单时的商品名称
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder} 需要变得更长
     */
    @Column(length = 240)
    private String goodName;
    /**
     * "6个A,7个B"
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @Lob
    private String orderBody;
    /**
     * 暂停结算
     */
    private boolean disableSettlement;
    /**
     * 物流信息
     * 正在进行中或者已完成的物流
     */
    @OneToMany
    @OrderBy("createTime desc")
    private List<StockShiftUnit> logisticsSet;
//    /**
//     * 所有物流信息
//     * 包括已被拒绝接单的物流
//     *
//     * @since {@link cn.lmjia.market.core.Version#muPartShift}
//     */
//    @OneToMany
//    private List<StockShiftUnit> allLogisticsSet;
    /**
     * 冗余设计，是否允许发货；它会在物流状态发生变化之后改变；
     *
     * @since {@link cn.lmjia.market.core.Version#muPartShift}
     */
    private boolean ableShip;
    /**
     * 已完成安装的物流
     *
     * @since {@link cn.lmjia.market.core.Version#muPartShift}
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany
    @JoinTable(name = "MAINORDER_INSTALLED_STOCKSHIFTUNIT")
    private List<StockShiftUnit> installedLogisticsSet;
    /**
     * 从{@link cn.lmjia.market.core.Version#muPartShift}开始放弃
     */
    @Deprecated
    @ManyToOne
    private StockShiftUnit currentLogistics;

//    /**
//     * @param from order表
//     * @return 到客户的登录表的关联
//     */
//    @Deprecated
//    public static Join<Customer, Login> getCustomerLogin(From<?, MainOrder> from) {
//        return getCustomer(from).join(Customer_.login);
//    }
    /**
     * 是否使用花呗支付
     */
    private boolean huabei;

    /**
     * @param from order表
     * @return 到下单者的登录表的关联
     */
    public static Join<MainOrder, Login> getOrderByLogin(From<?, MainOrder> from) {
        return from.join("orderBy");
    }

    public static Join<MainOrder, Customer> getCustomer(From<?, MainOrder> from) {
        return from.join("customer");
    }

    /**
     * @param path 订单from
     * @param cb   cb
     * @return 订单价格的查询
     */
    public static Expression<BigDecimal> getOrderDueAmount(From<?, MainOrder> path, CriteriaBuilder cb) {
        return path.get(MainOrder_.goodTotalPriceAmountIndependent);
//        return cb.toBigDecimal(cb.prod(
//                MainGood.getTotalPrice(path.join(MainOrder_.good), cb)
//                , path.get(MainOrder_.amount)));
    }

    /**
     * @param root            实体
     * @param criteriaBuilder cb
     * @return 业务订单号表达式
     * @see #getSerialId()
     */
    public static Expression<String> getSerialId(Path<MainOrder> root, CriteriaBuilder criteriaBuilder) {
        Expression<String> daily = JpaFunctionUtils.leftPaddingWith(criteriaBuilder, root.get("dailySerialId"), MaxDailySerialIdBit, '0');
        // 然后是日期
        Path<LocalDateTime> orderTime = root.get("orderTime");
        // https://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_date-format
        // date_format(current_date(),'%Y%m%d');
        Expression<String> year = criteriaBuilder.function("year", String.class, orderTime);
        Expression<String> month = JpaFunctionUtils.leftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("month", String.class, orderTime), 2, '0'
        );
        Expression<String> day = JpaFunctionUtils.leftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("day", String.class, orderTime), 2, '0'
        );
        return criteriaBuilder.concat(
                criteriaBuilder.concat(year, month)
                , criteriaBuilder.concat(day, daily)
        );
    }

    /**
     * @return 总的数量
     */
    public int getTotalAmount() {
        return amounts.values().stream()
                .mapToInt(value -> value)
                .sum();
    }

    /**
     * 创建下单记录
     */
    public void makeRecord() {
        if (record != null)
            throw new IllegalStateException("I really have a record!");
        record = new MainOrderRecord();
        record.setOrderTime(orderTime);
        record.setAge(LocalDate.now().getYear() - customer.getBirthYear());
        record.setGender(customer.getGender());
        record.setInstallAddress(installAddress);
        record.setMobile(customer.getMobile());
        record.setMortgageIdentifier(mortgageIdentifier);
        record.setName(customer.getName());
        record.updateAmounts(amounts);
        if (recommendBy != null)
            record.setRecommendByMobile(recommendBy.getLoginName());

//        setGoodTotalPrice(good.getTotalPrice());
        setGoodTotalPriceAmountIndependent(withAmount(MainGood::getTotalPrice));
//        setGoodName(good.getProduct().getName());
        setGoodName(amounts.keySet().stream()
                .map(good1 -> good1.getProduct().getName())
                .collect(Collectors.joining(",")));
//        setGoodCommissioningPrice(good.getProduct().getDeposit());
        setGoodCommissioningPriceAmountIndependent(withAmount(good1 -> good1.getProduct().getDeposit()));
        setOrderBody(amounts.entrySet().stream()
                .map(entry
                        -> entry.getValue()
                        + (StringUtils.isEmpty(entry.getKey().getProduct().getUnit())
                        ? "个" : entry.getKey().getProduct().getUnit())
                        + entry.getKey().getProduct().getName())
                .collect(Collectors.joining(",")));
    }

    /**
     * 结合数量结算金额
     *
     * @param function 每个商品所牵涉金额
     * @return 总牵涉金额
     */
    private BigDecimal withAmount(Function<MainGood, BigDecimal> function) {
        BigDecimal current = BigDecimal.ZERO;
        for (MainGood good : amounts.keySet()) {
            BigDecimal one = function.apply(good);
            current = current.add(one.multiply(BigDecimal.valueOf(amounts.get(good))));
        }
        return current;
    }

    @Override
    public Serializable getPayableOrderId() {
        return "main-" + id;
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        return goodTotalPriceAmountIndependent;
    }

    public Money getOrderDueAmountMoney() {
        return new Money(getOrderDueAmount());
    }

    @Override
    public String getOrderProductName() {
        return goodName;
    }

    @Override
    public String getOrderProductModel() {
        return getOrderProductCode();
    }

    @Override
    public String getOrderProductBrand() {
        // 随便找一个有品牌的 然后有多个就等
        return amounts.keySet().stream()
                .map(good1 -> good1.getProduct().getBrand())
                .filter(name -> !StringUtils.isEmpty(name))
                .findFirst()
                .orElse(getOrderProductName());
//        return StringUtils.isEmpty(getGood().getProduct().getBrand()) ? getOrderProductName()
//                : getGood().getProduct().getBrand();
    }

    @Override
    public String getOrderedName() {
        return getCustomer().getName();
    }

    @Override
    public String getOrderedMobile() {
        return getCustomer().getMobile();
    }

    @Override
    public String getOrderProductCode() {
        return amounts.keySet().stream()
                .map(good1 -> good1.getProduct().getCode())
                .filter(name -> !StringUtils.isEmpty(name))
                .findFirst()
                .orElse("无");
//        return getGood().getProduct().getCode();
    }

    /**
     * @return 业务订单号
     * @see #getSerialId(Path, CriteriaBuilder)
     */
    public String getSerialId() {
        return orderTime.format(SerialDateTimeFormatter)
                + String.format("%0" + MaxDailySerialIdBit + "d", dailySerialId);
    }

    public boolean isPay() {
        return orderStatus != OrderStatus.EMPTY && orderStatus != OrderStatus.forPay;
    }

    @Override
    public BigDecimal getCommissioningAmount() {
        return goodCommissioningPriceAmountIndependent;
    }

    @Override
    public Object lockObject() {
        return ("mainOrder-" + id).intern();
    }

    @Override
    public String getProvince() {
        return installAddress.getProvince();
    }

    @Override
    public String getCity() {
        return installAddress.getPrefecture();
    }

    @Override
    public String getCountry() {
        return installAddress.getCounty();
    }

    @Override
    public String getDetailAddress() {
        return installAddress.getOtherAddress();
    }

    @Override
    public String getConsigneeName() {
        return customer.getName();
    }

    @Override
    public String getConsigneeMobile() {
        return customer.getMobile();
    }

    /**
     * @return 需要物流的信息
     */
    public Map<MainProduct, Integer> getRequireShip() {
        final Map<MainProduct, Integer> require = new HashMap<>();
        amounts.forEach((good, integer) -> {
            if (require.putIfAbsent(good.getProduct(), integer) != null) {
                require.computeIfPresent(good.getProduct(), ((product, integer1) -> integer1 + integer));
            }
        });
        logisticsSet.stream().filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                .forEach(stockShiftUnit
                        -> stockShiftUnit.getAmounts().forEach(((product, productBatch) -> {
                    // 减去 require
                    MainProduct mainProduct = (MainProduct) product;
                    require.computeIfPresent(mainProduct, ((product1, integer) -> {
                        int now = integer - productBatch.getAmount();
                        if (now > 0)
                            return now;
                        if (now < 0)
                            log.error(getSerialId() + "诡异了，已物流的总量大于总的需物流量:" + mainProduct.getCode());
                        return null;
                    }));
                })));

        return require;
    }

    /**
     * 更新物流冗余信息并且切换当前状态
     * 可能切换为forInstall,forDeliver
     *
     * @return 是否都已完成物流（不包括安装）
     */
    public boolean updateLogisticsStatus() {
        // 是否所有订单都已失败
        if (logisticsSet.stream().allMatch(stockShiftUnit -> stockShiftUnit.getCurrentStatus() == ShiftStatus.reject)) {
            log.debug("所有物流订单都已被拒绝接单，重新进入待发货状态:" + getSerialId());
            setAbleShip(true);
            setOrderStatus(OrderStatus.forDeliver);
            return false;
        } else if (getRequireShip().isEmpty()) {
            log.debug("已物流所有所需货品:" + getSerialId());
            setAbleShip(false);
            // 已经无货需发了；如果还有货可发状态就无需关注了。
            // 现在确定是否都已经发完了
            if (logisticsSet.stream()
                    .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                    .allMatch(stockShiftUnit -> stockShiftUnit.getCurrentStatus() == ShiftStatus.success)) {
                setOrderStatus(OrderStatus.forInstall);
                log.debug("同时所有物流已抵达");
                return true;
            } else
                log.debug("但是并非所有物流已抵达");
        } else {
            log.debug("还有部分物流未发:" + getSerialId());
            setAbleShip(true);
        }
        return false;
    }

    /**
     * 增加已安装的物流信息
     * 可能切换为afterSale
     *
     * @param unit 已完成安装的物流;可能为null
     * @return 是否都已完成物流（包括安装）
     */
    public boolean updateInstallationStatus(StockShiftUnit unit) {
        if (getInstalledLogisticsSet() == null)
            setInstalledLogisticsSet(new ArrayList<>());

        if (unit != null) {
            getInstalledLogisticsSet().add(unit);
        }
        if (getRequireShip().isEmpty()) {
            log.debug("已物流所有所需货品:" + getSerialId());
            // 要么无需安装 要么已安装
            if (getLogisticsSet().stream()
                    .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                    .allMatch(stockShiftUnit ->
                            !stockShiftUnit.isInstallation() || getInstalledLogisticsSet().contains(stockShiftUnit))) {
                setOrderStatus(OrderStatus.afterSale);
                log.debug("并且所有物流都已完成安装或者无需安装");
                return true;
            } else if (log.isDebugEnabled())
                log.debug("但是并非所有物流订单都已完成安装或者无需安装");
        }
        return false;
    }
}
