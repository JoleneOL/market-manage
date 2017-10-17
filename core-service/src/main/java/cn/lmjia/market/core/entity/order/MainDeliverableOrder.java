package cn.lmjia.market.core.entity.order;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.record.MainOrderRecord;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.model.TimeLineUnit;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 我方可运递的订单
 * 包括商城订单，赠送订单以及批货订单
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MainDeliverableOrder implements LogisticsDestination, DeliverableOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 原始记录
     */
    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private MainOrderRecord record;

    private Address installAddress;
    /**
     * 获取者
     */
    @ManyToOne
    private Customer customer;
    /**
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @ElementCollection
    private Map<MainGood, Integer> amounts;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime orderTime;
    /**
     * 订单状态
     */
    private OrderStatus orderStatus;

    /**
     * 下单时的总价，该总价不依赖于数量即已经被完整计算
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal goodTotalPriceAmountIndependent;

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
     * 冗余设计，是否允许发货；它会在物流状态发生变化之后改变；
     *
     * @since {@link cn.lmjia.market.core.Version#muPartShift}
     */
    private boolean ableShip = true;

    /**
     * 物流信息
     * 正在进行中或者已完成的物流
     */
    @OneToMany
    @OrderBy("createTime desc")
    private List<StockShiftUnit> logisticsSet;
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
     * 结合数量结算金额
     *
     * @param function 每个商品所牵涉金额
     * @return 总牵涉金额
     */
    protected BigDecimal withAmount(Function<MainGood, BigDecimal> function) {
        BigDecimal current = BigDecimal.ZERO;
        for (MainGood good : amounts.keySet()) {
            BigDecimal one = function.apply(good);
            current = current.add(one.multiply(BigDecimal.valueOf(amounts.get(good))));
        }
        return current;
    }

    /**
     * 创建下单记录
     */
    public void makeRecord() {
        if (record != null)
            throw new IllegalStateException("I really have a record!");
        record = new MainOrderRecord();
        record.setOrderTime(getOrderTime());
        record.setAge(LocalDate.now().getYear() - getCustomer().getBirthYear());
        record.setGender(getCustomer().getGender());
        record.setInstallAddress(getInstallAddress());
        record.setMobile(getCustomer().getMobile());

        record.setName(getCustomer().getName());
        record.updateAmounts(getAmounts());

//        setGoodTotalPrice(good.getTotalPrice());
        setGoodTotalPriceAmountIndependent(withAmount(MainGood::getTotalPrice));
//        setGoodName(good.getProduct().getName());
        setGoodName(amounts.keySet().stream()
                .map(good1 -> good1.getProduct().getName())
                .collect(Collectors.joining(",")));
//        setGoodCommissioningPrice(good.getProduct().getDeposit());
        setOrderBody(amounts.entrySet().stream()
                .map(entry
                        -> entry.getValue()
                        + (StringUtils.isEmpty(entry.getKey().getProduct().getUnit())
                        ? "个" : entry.getKey().getProduct().getUnit())
                        + entry.getKey().getProduct().getName())
                .collect(Collectors.joining(",")));

    }

    /**
     * @return 被认可支付的时间
     */
    public abstract LocalDateTime getOrderPayTime();

    /**
     * @return 是否认可支付成功
     */
    public abstract boolean isPay();

    /**
     * @return 人类可读的id
     */
    public abstract String getHumanReadableId();

    /**
     * @return 总的数量
     */
    public int getTotalAmount() {
        return amounts.values().stream()
                .mapToInt(value -> value)
                .sum();
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

    @Override
    public void addInstallStockShiftUnit(StockShiftUnit unit) {
        if (getInstalledLogisticsSet() == null)
            setInstalledLogisticsSet(new ArrayList<>());
        getInstalledLogisticsSet().add(unit);
    }

    @Override
    public void addStockShiftUnit(StockShiftUnit unit) {
        if (getLogisticsSet() == null) {
            setLogisticsSet(new ArrayList<>());
        }
        getLogisticsSet().add(unit);
    }

    @Override
    public List<? extends StockShiftUnit> getInstallStockShiftUnit() {
        return getInstalledLogisticsSet();
    }

    @Override
    public Map<? extends Product, Integer> getTotalShipProduct() {
        final Map<MainProduct, Integer> require = new HashMap<>();
        amounts.forEach((good, integer) -> {
            if (require.putIfAbsent(good.getProduct(), integer) != null) {
                require.computeIfPresent(good.getProduct(), ((product, integer1) -> integer1 + integer));
            }
        });
        return require;
    }

    @Override
    public List<StockShiftUnit> getShipStockShiftUnit() {
        return getLogisticsSet();
    }

    @Override
    public void switchToForInstallStatus() {
        setOrderStatus(OrderStatus.forInstall);
    }

    @Override
    public void switchToForDeliverStatus() {
        setOrderStatus(OrderStatus.forDeliver);
    }

    @Override
    public void switchToStartDeliverStatus() {
        if (getOrderStatus() == OrderStatus.forDeliver)
            setOrderStatus(OrderStatus.forDeliverConfirm);
    }

    @Override
    public void switchToLogisticsFinishStatus() {
        setOrderStatus(OrderStatus.afterSale);
    }

    @Override
    public LogisticsDestination getLogisticsDestination() {
        return this;
    }

    @Override
    public Serializable getRepresentationalId() {
        return getId();
    }

    /**
     * @return 关于订单流水的简单生命线
     */
    public List<TimeLineUnit> getSimpleTimeLines() {
        // 大致定义是  支付，物流发货，物流完成，结算完成
        List<TimeLineUnit> list = new ArrayList<>();
        list.add(new TimeLineUnit("支付订单", getOrderPayTime(), isPay(), true));
        final LocalDateTime firstShipTime = getLogisticsSet().stream()
                .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                .map(StockShiftUnit::getCreateTime)
                .min(LocalDateTime::compareTo).orElse(null);
        list.add(new TimeLineUnit("物流发货", firstShipTime, firstShipTime != null, !isPay()));
        final LocalDateTime lastShipDoneTime = getLogisticsSet().stream()
                .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() == ShiftStatus.success)
                .map(StockShiftUnit::getCreateTime)
                .max(LocalDateTime::compareTo).orElse(null);
        list.add(new TimeLineUnit("物流交付", lastShipDoneTime, lastShipDoneTime != null, firstShipTime == null));
        list.add(new TimeLineUnit("佣金结算", lastShipDoneTime, lastShipDoneTime != null, firstShipTime == null));

        return list;
    }

    public Money getOrderDueAmountMoney() {
        return new Money(getGoodTotalPriceAmountIndependent());
    }
}
