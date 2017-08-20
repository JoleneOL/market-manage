package cn.lmjia.market.core.event;

import cn.lmjia.market.core.entity.MainOrder;
import lombok.Data;
import me.jiangcai.logistics.event.ShiftEvent;

/**
 * 主订单送达事件，总是因为物流事件导致的
 *
 * @author CJ
 */
@Data
public class MainOrderDeliveredEvent {
    /**
     * 事务内订单
     */
    private final MainOrder mainOrder;
    /**
     * 可选物流事件
     */
    private final ShiftEvent source;
}
