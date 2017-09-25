package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.service.PayableSystemService;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 我们自己的支付服务
 *
 * @author CJ
 */
public interface PayService extends PayableSystemService {

    @EventListener(OrderPaySuccess.class)
    @Transactional
    void paySuccess(OrderPaySuccess event);

    @EventListener(OrderPayCancellation.class)
    @Transactional
    void payCancel(OrderPayCancellation event);

    /**
     * @param request  当前请求
     * @param order    主订单
     * @param payOrder 可选的支付订单
     * @return 该订单成功被支付之后展示的URI
     */
    String mainOrderPaySuccessUri(HttpServletRequest request, MainOrder order, PayOrder payOrder);

}
