package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
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

    /**
     * login刚刚加入，如果存在引导者 请给他发布通知
     *
     * @param login  login
     * @param mobile 它的手机号码
     */
    void newLogin(Login login, String mobile);


}
