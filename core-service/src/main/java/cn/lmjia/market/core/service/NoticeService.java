package cn.lmjia.market.core.service;

import me.jiangcai.payment.event.OrderPaySuccess;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

/**
 * 负责消息发布
 *
 * @author CJ
 */
public interface NoticeService {

    @PostConstruct
    void init();

    // 订单支付时发送事件
    @EventListener(OrderPaySuccess.class)
    void orderPaySuccess(OrderPaySuccess event);

}
