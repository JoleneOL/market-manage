package cn.lmjia.market.core.entity.order;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
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
@SuppressWarnings("JpaDataSourceORMInspection")
@AssociationOverrides(
        {
                @AssociationOverride(
                        name = "installedLogisticsSet"
                        , joinTable = @JoinTable(name = "AgentPrepaymentOrder_INSTALLED_STOCKSHIFTUNIT")
                )
        }
)
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

    @Override
    public String getHumanReadableId() {
        return "货款发货" + getId();
    }

    @Override
    public Login getOrderPerson() {
        return belongs;
    }
}
