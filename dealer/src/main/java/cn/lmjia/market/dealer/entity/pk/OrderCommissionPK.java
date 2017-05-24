package cn.lmjia.market.dealer.entity.pk;

import cn.lmjia.market.core.entity.MainOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCommissionPK implements Serializable {

    private long source;
    private boolean refund;

    public OrderCommissionPK(MainOrder order) {
        this(order, false);
    }

    public OrderCommissionPK(MainOrder order, boolean refund) {
        source = order.getId();
        this.refund = refund;
    }
}
