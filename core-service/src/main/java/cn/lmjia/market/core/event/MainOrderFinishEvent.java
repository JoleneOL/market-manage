package cn.lmjia.market.core.event;

import cn.lmjia.market.core.entity.MainOrder;
import lombok.Data;
import me.jiangcai.lib.thread.ThreadLocker;
import me.jiangcai.logistics.event.InstallationEvent;

/**
 * 订单完成时间，可以引发诸如佣金收益之类的；总是因为安装事件导致的
 *
 * @author CJ
 */
@Data
public class MainOrderFinishEvent implements ThreadLocker {
    /**
     * 事务内订单
     */
    private final MainOrder mainOrder;
    /**
     * 可选
     */
    private final InstallationEvent source;

    @Override
    public Object lockObject() {
        return mainOrder.lockObject();
    }
}
