package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.util.CommissionSource;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.lib.thread.ThreadLocker;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainOrder extends MainDeliverableOrder implements PayableOrder, CommissionSource, ThreadLocker
        , LogisticsDestination, DeliverableOrder {
    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);
    /**
     * 最长长度
     */
    private static final int MaxDailySerialIdBit = 6;
    private static final Log log = LogFactory.getLog(MainOrder.class);
    @OneToOne
    private SalesAchievement salesAchievement;
    /**
     * 每日序列号
     * 业务量每天不会超过1000000
     * 所以这个号在整体订单号中长度为 6
     */
    private int dailySerialId;
    /**
     * 谁下的单，并不意味着它即可获得收益，需要确保该用户是否是正式用户（即下过一单或者是一个代理商，如果不是则给他的引导者）
     */
    @ManyToOne
    private Login orderBy;
    /**
     * 谁推荐的
     */
    @ManyToOne
    private Login recommendBy;
    /**
     * 具体的产品
     */
    @ManyToOne
    @Deprecated
    private MainGood good;
    @Deprecated
    private int amount;
    /**
     * 按揭识别码
     */
    @Column(length = 32)
    private String mortgageIdentifier;
    /**
     * 成功支付的支付订单
     */
    @ManyToOne
    private PayOrder payOrder;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime payTime;
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
     * 下单时用于结算佣金的价格
     * 将在{@link #makeRecord()}时被记录
     *
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal goodCommissioningPriceAmountIndependent;
    /**
     * 暂停结算
     */
    private boolean disableSettlement;
    /**
     * 从{@link cn.lmjia.market.core.Version#muPartShift}开始放弃
     */
    @Deprecated
    @ManyToOne
    private StockShiftUnit currentLogistics;
    /**
     * 是否使用花呗支付
     */
    private boolean huabei;

//    /**
//     * @param from order表
//     * @return 到客户的登录表的关联
//     */
//    @Deprecated
//    public static Join<Customer, Login> getCustomerLogin(From<?, MainOrder> from) {
//        return getCustomer(from).join(Customer_.login);
//    }

    /**
     * @param root root
     * @param cb   cb
     * @return 订单成功支付的条件
     * @see #isPay()
     */
    public static Predicate getOrderPaySuccess(From<?, MainOrder> root, CriteriaBuilder cb) {
        final Path<OrderStatus> statusPath = root.get(MainOrder_.orderStatus);
        return cb.and(
                cb.notEqual(statusPath, OrderStatus.EMPTY)
                , cb.notEqual(statusPath, OrderStatus.forPay)
                , cb.notEqual(statusPath, OrderStatus.close)
        );
    }

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
     * @param str {@link #getPayableOrderId()}
     * @return 将该str转换为订单id；null表示无法解释或者非本类订单
     */
    public static Long payableOrderIdToId(String str) {
        if (StringUtils.isEmpty(str))
            return null;
        if (!str.startsWith("main-"))
            return null;
        return NumberUtils.parseNumber(str.substring("main-".length()), Long.class);
    }

    @Override
    public void makeRecord() {
        super.makeRecord();

        getRecord().setMortgageIdentifier(mortgageIdentifier);
        if (recommendBy != null)
            getRecord().setRecommendByMobile(recommendBy.getLoginName());

        setGoodCommissioningPriceAmountIndependent(withAmount(good1 -> good1.isCommissionSource()
                ? good1.getProduct().getDeposit() : BigDecimal.ZERO));
    }

    @Override
    public Serializable getPayableOrderId() {
        return "main-" + getId();
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        return getGoodTotalPriceAmountIndependent();
    }

    public Money getCommissioningAmountMoney() {
        return new Money(getCommissioningAmount());
    }

    @Override
    public String getOrderProductName() {
        return getGoodName();
    }

    @Override
    public String getOrderProductModel() {
        return getOrderProductCode();
    }

    @Override
    public String getOrderProductBrand() {
        // 随便找一个有品牌的 然后有多个就等
        return getAmounts().keySet().stream()
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
        return getAmounts().keySet().stream()
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
        return getOrderTime().format(SerialDateTimeFormatter)
                + String.format("%0" + MaxDailySerialIdBit + "d", dailySerialId);
    }

    @Override
    public boolean isPay() {
        return getOrderStatus() != OrderStatus.EMPTY && getOrderStatus() != OrderStatus.forPay && getOrderStatus() != OrderStatus.close;
    }

    @Override
    public BigDecimal getCommissioningAmount() {
        return goodCommissioningPriceAmountIndependent;
    }

    @Override
    public Object lockObject() {
        return ("mainOrder-" + getId()).intern();
    }

    @Override
    public LocalDateTime getOrderPayTime() {
        return getPayTime();
    }

    @Override
    public String toString() {
        return "MainOrder(" + getSerialId() + ":" + getId() + ")";
    }
}
