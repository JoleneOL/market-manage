package cn.lmjia.market.core.event;

import cn.lmjia.market.core.entity.MainOrder;
import lombok.Data;
import me.jiangcai.lib.thread.ThreadLocker;

/**
 * 订单完成时间，可以引发诸如佣金收益之类的
 *
 * @author CJ
 */
@Data
public class MainOrderFinishEvent implements ThreadLocker {
    private final MainOrder mainOrder;

    @Override
    public Object lockObject() {
        return mainOrder.asLocker();
    }
}
