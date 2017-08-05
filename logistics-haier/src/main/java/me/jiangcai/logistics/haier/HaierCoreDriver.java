package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.haier.model.OrderStatusSync;
import me.jiangcai.logistics.haier.model.OutInStore;
import me.jiangcai.logistics.haier.model.RejectInfo;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 核心驱动
 *
 * @author CJ
 */
public interface HaierCoreDriver {

    /**
     * 3.3.3、采购入库单、销售出库单的入库、出库流水信息通过此接口回传给客户。（排除退货入库的流水，其他流水都是通过这个接口）
     *
     * @param event
     */
    @EventListener(OutInStore.class)
    @Transactional
    void forOutInStore(OutInStore event);

    /**
     * 3.3.2、采购入库单、销售出库单相关订单的入库、出库过程中的订单状态日日顺都通过这个接口回传给客户
     * ，具体订单状态对应文档14页 status字段，具体描述参见本文档下面部分。（全部的订单状态都是通过这个接口回传）
     *
     * @param event
     */
    @EventListener(OrderStatusSync.class)
    @Transactional
    void forOrderStatusSync(OrderStatusSync event);

    /**
     * 3.3.6、退货入库流水回传：货物送达用户，用户发生拒收，日日顺会生成退货入库单，退货入库完成后通过此接口将退货入库流水回传给客户。
     *
     * @param event
     */
    @EventListener(RejectInfo.class)
    @Transactional
    void forRejectInfo(RejectInfo event);

}
