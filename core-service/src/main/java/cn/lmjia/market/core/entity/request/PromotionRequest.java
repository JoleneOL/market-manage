package cn.lmjia.market.core.entity.request;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提升申请
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PromotionRequest implements PayableOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 谁的申请
     */
    @ManyToOne
    private Login whose;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime requestTime;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime changeTime;
    @ManyToOne
    private Manager changer;
    private PromotionRequestStatus requestStatus;
    private PaymentStatus paymentStatus;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime PayTime;

    /**
     * 成功支付的支付订单
     */
    @ManyToOne
    private PayOrder payOrder;
    /**
     * 需支付金额 可为null
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal price;
    /**
     * 公司名称
     */
    private String name;
    private Address address;
    /**
     * 类型,不知道以后还有什么 就用int了
     * 1: 经销商
     * 2: 代理商
     * 3: 省代理（其实是区代理）
     */
    private int type;
    @Column(length = 60)
    private String frontImagePath;
    @Column(length = 60)
    private String backImagePath;
    @Column(length = 68)
    private String businessLicensePath;


    @Override
    public Serializable getPayableOrderId() {
        return "PromotionRequest-" + getId();
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        if (type == 1)
            return price;
        return null;
    }

    @Override
    public String getOrderProductName() {
        return "经销商开通费";
    }

    @Override
    public String getOrderBody() {
        return "经销商开通费";
    }
}
