package cn.lmjia.market.core.service;

import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.service.PayableSystemService;
import org.springframework.context.event.EventListener;

/**
 * 我们自己的支付服务
 *
 * @author CJ
 */
public interface PayService extends PayableSystemService {

    @EventListener(OrderPaySuccess.class)
    void paySuccess(OrderPaySuccess event);

    @EventListener(OrderPayCancellation.class)
    void payCancel(OrderPayCancellation event);

}
