package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 这是一种临时解决方案
 *
 * @author CJ
 */
public interface QuickTradeService {

    /**
     * 让一笔支付完成的订单立刻进入完成状态，并且完成佣金结算
     *
     * @param order 已完成支付的订单F
     */
    @Transactional
    void makeDone(MainOrder order);

}
