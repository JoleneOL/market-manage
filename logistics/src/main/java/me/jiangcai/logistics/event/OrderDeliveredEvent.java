package me.jiangcai.logistics.event;

import lombok.Data;
import me.jiangcai.logistics.DeliverableOrder;

/**
 * 订单物流全部抵达事件
 *
 * @author CJ
 */
@Data
public class OrderDeliveredEvent {
    /**
     * 事务内订单
     */
    private final DeliverableOrder order;
    /**
     * 可选物流事件
     */
    private final ShiftEvent source;
}
