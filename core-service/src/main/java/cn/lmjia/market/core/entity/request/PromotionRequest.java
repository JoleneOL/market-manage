package cn.lmjia.market.core.entity.request;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 提升申请
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PromotionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 谁的申请
     */
    @ManyToOne
    private Login whose;
    private PromotionRequestStatus requestStatus;
    private PaymentStatus paymentStatus;

    /**
     * 成功支付的支付订单
     */
    @ManyToOne
    private PayOrder payOrder;
    /**
     * 公司名称
     */
    private String name;


}
