package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.event.MainOrderFinishEvent;
import me.jiangcai.lib.thread.ThreadSafe;
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

}
