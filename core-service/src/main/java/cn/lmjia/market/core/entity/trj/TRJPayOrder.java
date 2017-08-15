package cn.lmjia.market.core.entity.trj;

import cn.lmjia.market.core.trj.TRJService;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class TRJPayOrder extends PayOrder {

    @OneToOne
    private AuthorisingInfo authorisingInfo;

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return TRJService.class;
    }
}
