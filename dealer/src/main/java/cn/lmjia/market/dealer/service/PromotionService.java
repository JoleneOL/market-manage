package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.event.LoginRelationChangedEvent;
import me.jiangcai.lib.thread.ThreadSafe;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 晋升服务
 *
 * @author CJ
 */
public interface PromotionService {

    /**
     * @param event 触发事件
     * @return 是否迭代产生新的事件
     */
    @Transactional
    @EventListener(LoginRelationChangedEvent.class)
    @ThreadSafe
    LoginRelationChangedEvent tryPromotion(LoginRelationChangedEvent event);

    /**
     * 如果level为最低代理商，就是客户晋升为最低代理商所需的有效用户数量
     *
     * @param level 特定等级
     * @return 晋级成为level级的代理商所需的 推荐同级别数量
     */
    @Transactional(readOnly = true)
    int promotionCountForAgentLevel(int level);

    /**
     * 更新{@link #promotionCountForAgentLevel(int)}
     */
    @Transactional
    void updatePromotionCountForAgentLevel(int level, int count);

}
