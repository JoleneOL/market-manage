package cn.lmjia.market.core.entity.order;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 代理商预付款订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class AgentPrepaymentOrder extends MainDeliverableOrder {
    /**
     * 哪个代理商
     */
    @ManyToOne
    private Login belongs;

    @Override
    public LocalDateTime getOrderPayTime() {
        return getOrderTime();
    }

    @Override
    public boolean isPay() {
        return true;
    }
}
