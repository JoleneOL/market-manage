package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.PayService;
import lombok.SneakyThrows;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class PayServiceImpl implements PayService {

    private static final Log log = LogFactory.getLog(PayServiceImpl.class);

    @Autowired
    private MainOrderService mainOrderService;

    @Override
    public ModelAndView paySuccess(PayableOrder payableOrder, PayOrder payOrder) {
        MainOrder mainOrder = (MainOrder) payableOrder;
        return new ModelAndView("redirect:/paySuccess?mainOrderId=" + mainOrder.getId() + "&payOrderId=" + payOrder.getId());
    }

    @Override
    @SneakyThrows
    public ModelAndView pay(PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters) {
        MainOrder mainOrder = (MainOrder) order;

        return new ModelAndView("redirect:/paying?mainOrderId=" + mainOrder.getId() + "&payOrderId="
                + payOrder.getId());
        //"&checkUri="
//        + URLEncoder.encode(additionalParameters.get("checkUri").toString(), "UTF-8") + "&successUri="
//                + URLEncoder.encode(additionalParameters.get("successUri").toString(), "UTF-8")
    }

    @Override
    public boolean isPaySuccess(String id) {
        // 前面的main-去去掉
        long orderId = toOrderId(id);
        return mainOrderService.isPaySuccess(orderId);
    }

    private long toOrderId(String text) {
        return NumberUtils.parseNumber(text.split("-")[1], Long.class);
    }

    @Override
    public PayableOrder getOrder(String id) {
        return mainOrderService.getOrder(toOrderId(id));
    }

    @Override
    @EventListener(OrderPaySuccess.class)
    public void paySuccess(OrderPaySuccess event) {
        MainOrder mainOrder = (MainOrder) event.getPayableOrder();
        if (mainOrder.isPay())
            throw new IllegalStateException("订单已支付");
        mainOrder.setPayTime(LocalDateTime.now());
        mainOrder.setOrderStatus(OrderStatus.forDeliver);
        mainOrder.setPayOrder(event.getPayOrder());
    }

    @Override
    @EventListener(OrderPayCancellation.class)
    public void payCancel(OrderPayCancellation event) {
        log.warn(event.getPayableOrder() + "放弃了支付");
    }
}
