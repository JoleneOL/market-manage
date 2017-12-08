package cn.lmjia.market.core.service;

import me.jiangcai.logistics.event.DeliveryGoodsSuccessEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发货后,给收货人发送通知短信.
 * @author lxf
 */

public interface DeliveryNoticeService {

    /**
     * 发货后的事件.
     * @param event
     */
    @EventListener(DeliveryGoodsSuccessEvent.class)
    @Transactional
    void sendNotification(DeliveryGoodsSuccessEvent event) throws ClassNotFoundException;

}
