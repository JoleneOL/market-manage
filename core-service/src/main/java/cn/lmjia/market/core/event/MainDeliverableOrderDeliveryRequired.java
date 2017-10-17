package cn.lmjia.market.core.event;

import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import lombok.Data;

/**
 * 该订单可发货了
 *
 * @author CJ
 */
@Data
public class MainDeliverableOrderDeliveryRequired {

    private final MainDeliverableOrder order;

}
