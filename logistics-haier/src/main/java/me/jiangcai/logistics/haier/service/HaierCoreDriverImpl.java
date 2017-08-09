package me.jiangcai.logistics.haier.service;

import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.haier.HaierCoreDriver;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import me.jiangcai.logistics.haier.model.OrderStatusSync;
import me.jiangcai.logistics.haier.model.OutInStore;
import me.jiangcai.logistics.haier.model.RejectInfo;
import me.jiangcai.logistics.haier.repository.HaierOrderRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author CJ
 */
@Component
public class HaierCoreDriverImpl implements HaierCoreDriver {

    private static final Log log = LogFactory.getLog(HaierCoreDriverImpl.class);
    @Autowired
    private HaierOrderRepository haierOrderRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void forOutInStore(OutInStore event) {
//3.3.3、采购入库单、销售出库单的入库、出库流水信息通过此接口回传给客户。（排除退货入库的流水，其他流水都是通过这个接口）
        // 需要确认是否正确 不然抓瞎啊？
        HaierOrder order = haierOrderRepository.findByOrderNumber(event.getOrderNo());
        if (order == null) {
            log.info("[HR] bad id with:" + event);
            throw new IllegalStateException("[HR] bad orderId:" + event.getOrderNo());
        }
        final String expressId = event.getExpNo();
        if (!StringUtils.isEmpty(expressId))
            order.setExpressId(expressId);

        // bustype	String(16)	必选	业务类型：1出库  2入库
        // 确认订单是否正确
        if ("1".equals(event.getType()) && order.getOrigin() == null) {
            throw new IllegalStateException("[HR]" + event.getOrderNo() + "为出库单，我方并不这么认为。");
        }
        if ("2".equals(event.getType()) && order.getDestination() == null) {
            throw new IllegalStateException("[HR]" + event.getOrderNo() + "为入库单，我方并不这么认为。");
        }

        if (event.isComplete()) {
            applicationEventPublisher.publishEvent(new ShiftEvent(order, ShiftStatus.success, event.getDate()
                    , event.getRemark(), event));
        } else {
            log.error("[HR] why fired un-completed OutInStore? " + event);
        }

    }

    @Override
    public void forOrderStatusSync(OrderStatusSync event) {
//        ok	WMS_ACCEPT -接单		客户下单给日日顺，日日顺接单成功
//        ok	WMS_FAILED -拒单		客户下单字段或规则不符合要求，解析订单失败，拒单
//        ok	TMS_ACCEPT 揽收		揽收订单确认已揽收到货物
//        ok	TMS_REJECT -揽收失败		揽收订单确认揽收失败，未揽收到货物
//        ok	TMS_DELIVERING -派送		货物已出库
//        ok	TMS_STATION_IN -分站进		需要转运的订单，货物已经进入转运仓
//        ok	TMS_STATION_OUT -分站出		需要转运的订单，货物已经从转运仓出库
//        无视	TMS_CHANGE -用户改约		用户发起改约信息，回传给客户
//        无视	TMS_ERROR -异常		配送延误中，人工录入的延误原因，通过此节点返回
//        ok	TMS_SIGN -签收成功		货物送达，用户签收
//        ok	TMS_FAILED -拒签		货物送达后，用户拒签
//        ok	TMS_DELIVERY-网点交付		日日顺物流将货物送达最后一公里的网点
//        ok	TMS_FAILED_IN -转运入库		逆向退货、取件单的 需要转运的订单，货物已经进入转运仓
//        ok	TMS_FAILED_OUT -转运出库		逆向退货、取件单的 需要转运的订单，货物已经从转运仓出库
//        ok	TMS_RETURN-拒收入库		用户拒收退货入库
//        ok	TMS_RESULT_S-取件成功		从用户家取件成功
//        ok	TMS_RESULT_F-取件失败		从用户家区间失败
//        无视	COD_SUCCESS 扣款成功		扣款
//        ok	TMS_DB_CREATE 调拨单生成		调拨单创建成功
//        ok	TMS_DB_OUT调拨单出库		调拨出库完成
//        ok	TMS_DB_IN 调拨单入库		调拨入库完成
        //ok  签收安装后又拒收 TMS_SIGN_AZ1_F
//        签收并安装 TMS_SIGN_AZ1
//       无视即可 签收未安装 TMS_SIGN_AZ0
// 3.3.2、采购入库单、销售出库单相关订单的入库、出库过程中的订单状态日日顺都通过这个接口回传给客户
// ，具体订单状态对应文档14页 status字段，具体描述参见本文档下面部分。（全部的订单状态都是通过这个接口回传）
        HaierOrder order = haierOrderRepository.findByOrderNumber(event.getOrderNo());
        if (order == null) {
            log.info("[HR] bad id with:" + event);
            throw new IllegalStateException("[HR] bad orderId:" + event.getOrderNo());
        }
        final String expressId = event.getExpNo();
        if (!StringUtils.isEmpty(expressId))
            order.setExpressId(expressId);

        final String status = event.getStatus();
        String message = "[" + event.getOperator() + "]" + event.getContent();
        ShiftStatus shiftStatus;
        // 关于安装信息 这个应该是一个额外事件！

        if ("WMS_ACCEPT".equalsIgnoreCase(status)
                || "TMS_ACCEPT".equalsIgnoreCase(status)
                || "TMS_DB_CREATE".equalsIgnoreCase(status)
                ) {
            shiftStatus = ShiftStatus.accept;
        } else if ("WMS_FAILED".equalsIgnoreCase(status)
                || "TMS_REJECT".equalsIgnoreCase(status)
                || "TMS_RETURN".equalsIgnoreCase(status)
                || "TMS_RESULT_F".equalsIgnoreCase(status)
                ) {
            shiftStatus = ShiftStatus.reject;
        } else if ("TMS_FAILED".equalsIgnoreCase(status)
                || "TMS_SIGN_AZ1_F".equalsIgnoreCase(status)
                ) {
            // 货物送达后，用户拒签
            shiftStatus = ShiftStatus.failed;
        } else if ("TMS_DELIVERING".equalsIgnoreCase(status)
                || "TMS_STATION_IN".equalsIgnoreCase(status)
                || "TMS_STATION_OUT".equalsIgnoreCase(status)
                || "TMS_DELIVERY".equalsIgnoreCase(status)
                || "TMS_FAILED_IN".equalsIgnoreCase(status)
                || "TMS_FAILED_OUT".equalsIgnoreCase(status)
                || "TMS_RESULT_S".equalsIgnoreCase(status)
                ) {
            shiftStatus = ShiftStatus.movement;
        } else if (status.startsWith("TMS_SIGN")
                || "TMS_DB_OUT".equalsIgnoreCase(status)
                || "TMS_DB_IN".equalsIgnoreCase(status)
                ) {
            shiftStatus = ShiftStatus.success;
        } else {
            shiftStatus = order.getCurrentStatus();
        }

        applicationEventPublisher.publishEvent(new ShiftEvent(order, shiftStatus, event.getOperateDate(), message, event));
        if ("TMS_SIGN_AZ1".equalsIgnoreCase(status)) {
            // 发布安装成功时间
            applicationEventPublisher.publishEvent(new InstallationEvent(order, null, null, null, event.getOperateDate()));
        }
    }

    @Override
    public void forRejectInfo(RejectInfo event) {
        //
        HaierOrder order = haierOrderRepository.findByOrderNumber(event.getOrderNo());
        if (order == null) {
            log.info("[HR] bad id with:" + event);
            throw new IllegalStateException("[HR] bad orderId:" + event.getOrderNo());
        }
        // 拒收完成，则订单应该切换到拒绝
        if (event.isComplete()) {
            applicationEventPublisher.publishEvent(new ShiftEvent(order, ShiftStatus.reject, event.getDate()
                    , event.getContent(), event));
        } else {
            log.error("[HR] why fired un-completed RejectInfo? " + event);
        }
    }
}
