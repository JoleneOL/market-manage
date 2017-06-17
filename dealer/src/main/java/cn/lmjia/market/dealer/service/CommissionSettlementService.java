package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import me.jiangcai.lib.thread.ThreadSafe;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 佣金结算
 *
 * @author CJ
 */
public interface CommissionSettlementService {

    @EventListener(MainOrderFinishEvent.class)
    @ThreadSafe
    @Transactional
    void orderFinish(MainOrderFinishEvent event);

    @EventListener(OrderPaySuccess.class)
    @Transactional
    void orderPaySuccess(OrderPaySuccess event);

    /**
     * 重新结算这个订单
     *
     * @param order 订单
     */
    @ThreadSafe
    @Transactional
    void reSettlement(MainOrder order);

}
