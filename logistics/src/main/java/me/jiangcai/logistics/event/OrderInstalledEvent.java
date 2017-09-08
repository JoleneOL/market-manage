package me.jiangcai.logistics.event;

import lombok.Data;
import me.jiangcai.logistics.DeliverableOrder;

/**
 * 订单物流全部抵达而且该安装已完成或者无需安装
 *
 * @author CJ
 */
@Data
public class OrderInstalledEvent {
    /**
     * 事务内订单
     */
    private final DeliverableOrder mainOrder;
    /**
     * 可选
     */
    private final InstallationEvent source;
}
