package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.record.MainOrderRecord;
import cn.lmjia.market.core.entity.support.Address;
import lombok.Getter;
import lombok.Setter;
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
public class MainOrder implements PayableOrder {
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
     * 谁下的单
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
     * 支付是否已完成
     */
    private boolean pay;

    /**
     * @param root            实体
     * @param criteriaBuilder cb
     * @return 业务订单号表达式
     * @see #getSerialId()
     */
    public static Expression<String> getSerialIdE(Path<MainOrder> root, CriteriaBuilder criteriaBuilder) {
        Expression<String> daily = LeftPaddingWith(root.get("dailySerialId"), criteriaBuilder, MaxDailySerialIdBit, '0');
        // 然后是日期
        Path<LocalDateTime> orderTime = root.get("orderTime");
        Expression<String> year = criteriaBuilder.function("year", String.class, orderTime);
        Expression<String> month = LeftPaddingWith(
                criteriaBuilder.function("month", String.class, orderTime), criteriaBuilder, 2, '0'
        );
        Expression<String> day = LeftPaddingWith(
                criteriaBuilder.function("day", String.class, orderTime), criteriaBuilder, 2, '0'
        );
        return criteriaBuilder.concat(
                criteriaBuilder.concat(year, month)
                , criteriaBuilder.concat(day, daily)
        );
    }

    /**
     * 左边填充
     *
     * @param to              来源表达式
     * @param criteriaBuilder cb
     * @param length          达到长度目标
     * @param with            使用什么字符填充
     * @return 「左边填充」的表达式
     */
    private static Expression<String> LeftPaddingWith(Expression to, CriteriaBuilder criteriaBuilder, int length, char with) {
        return criteriaBuilder.function("LPAD", String.class, to
                , criteriaBuilder.literal(length), criteriaBuilder.literal(with));
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
    }

    @Override
    public Serializable getPayableOrderId() {
        return "main-" + id;
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        return good.getTotalPrice().multiply(BigDecimal.valueOf(amount));
    }

    @Override
    public String getOrderProductName() {
        return good.getProduct().getName();
    }

    /**
     * @return 业务订单号
     * @see #getSerialIdE(Path, CriteriaBuilder)
     */
    public String getSerialId() {
        return orderTime.format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA))
                + String.format("%0" + MaxDailySerialIdBit + "d", dailySerialId);
    }
}
