package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.record.MainOrderRecord;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.util.CommissionSource;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.lib.thread.ThreadLocker;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
import java.util.Locale;

/**
 * 订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainOrder implements PayableOrder, CommissionSource, ThreadLocker {
    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);
    /**
     * 最长长度
     */
    private static final int MaxDailySerialIdBit = 6;
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
    private MainGood good;
    private int amount;
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
    @Column(scale = 2, precision = 12)
    private BigDecimal goodTotalPrice;
    /**
     * 下单时用于结算佣金的价格
     * 将在{@link #makeRecord()}时被记录
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal goodCommissioningPrice;
    /**
     * 下单时的商品名称
     * 将在{@link #makeRecord()}时被记录
     */
    @Column(length = 40)
    private String goodName;

    /**
     * 暂停结算
     */
    private boolean disableSettlement;

    /**
     * @param from order表
     * @return 到客户的登录表的关联
     */
    public static Join<MainOrder, Login> getCustomerLogin(From<?, MainOrder> from) {
        return getCustomer(from).join("login");
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

    public static Expression<BigDecimal> getOrderDueAmount(From<?, MainOrder> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.toBigDecimal(criteriaBuilder.prod(
                MainGood.getTotalPrice(path.join("good"), criteriaBuilder)
                , path.get("amount")));
    }

    /**
     * @param root            实体
     * @param criteriaBuilder cb
     * @return 业务订单号表达式
     * @see #getSerialId()
     */
    public static Expression<String> getSerialId(Path<MainOrder> root, CriteriaBuilder criteriaBuilder) {
        Expression<String> daily = JpaFunctionUtils.LeftPaddingWith(criteriaBuilder, root.get("dailySerialId"), MaxDailySerialIdBit, '0');
        // 然后是日期
        Path<LocalDateTime> orderTime = root.get("orderTime");
        // https://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_date-format
        // date_format(current_date(),'%Y%m%d');
        Expression<String> year = criteriaBuilder.function("year", String.class, orderTime);
        Expression<String> month = JpaFunctionUtils.LeftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("month", String.class, orderTime), 2, '0'
        );
        Expression<String> day = JpaFunctionUtils.LeftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("day", String.class, orderTime), 2, '0'
        );
        return criteriaBuilder.concat(
                criteriaBuilder.concat(year, month)
                , criteriaBuilder.concat(day, daily)
        );
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
        record.setAmount(amount);
        record.setGender(customer.getGender());
        record.setInstallAddress(installAddress);
        record.setMobile(customer.getMobile());
        record.setMortgageIdentifier(mortgageIdentifier);
        record.setName(customer.getName());
        record.setProductName(good.getProduct().getName());
        record.setProductType(good.getProduct().getCode());
        record.setRecommendByMobile(recommendBy.getLoginName());

        setGoodTotalPrice(good.getTotalPrice());
        setGoodName(good.getProduct().getName());
        setGoodCommissioningPrice(good.getProduct().getDeposit());
    }

    @Override
    public Serializable getPayableOrderId() {
        return "main-" + id;
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        return goodTotalPrice.multiply(BigDecimal.valueOf(amount));
    }

    public Money getOrderDueAmountMoney() {
        return new Money(getOrderDueAmount());
    }

    @Override
    public String getOrderProductName() {
        return goodName;
    }

    @Override
    public String getOrderBody() {
        return amount + "个" + goodName;
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
        return goodCommissioningPrice.multiply(BigDecimal.valueOf(amount));
    }

    @Override
    public Object lockObject() {
        return ("mainOrder-" + id).intern();
    }
}
