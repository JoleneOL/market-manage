package cn.lmjia.market.core.trj;

import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 投融家相关服务，我们也认可它是一种支付方式
 *
 * @author CJ
 */
public interface TRJService extends PaymentForm {

    /**
     * 添加一个有效按揭码
     *
     * @param authorising
     * @param idNumber
     */
    @Transactional
    void addAuthorisingInfo(String authorising, String idNumber);

    @EventListener(OrderPaySuccess.class)
    void paySuccess(OrderPaySuccess event);

    /**
     * 检查可用的按揭码
     *
     * @param authorising
     * @param idNumber
     * @return
     * @throws InvalidAuthorisingException
     */
    @Transactional(readOnly = true)
    AuthorisingInfo checkAuthorising(String authorising, String idNumber) throws InvalidAuthorisingException;
}
