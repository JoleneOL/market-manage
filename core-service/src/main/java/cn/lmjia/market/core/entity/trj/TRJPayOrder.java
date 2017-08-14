package cn.lmjia.market.core.entity.trj;

import lombok.Getter;
import lombok.Setter;
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
}
