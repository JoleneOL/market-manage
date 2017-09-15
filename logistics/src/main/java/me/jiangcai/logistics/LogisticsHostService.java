package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.StockShiftUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * 由项目代码提供的宿主服务
 *
 * @author CJ
 */
public interface LogisticsHostService {

    /**
     * @param unit 特定物流转移订单
     * @return 获取特定物流转移订单所相关的物流订单；可以返回null如果无关
     */
    @Transactional(readOnly = true)
    DeliverableOrder orderFor(StockShiftUnit unit);

}
