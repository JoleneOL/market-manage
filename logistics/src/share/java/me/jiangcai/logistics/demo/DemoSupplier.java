package me.jiangcai.logistics.demo;

import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public interface DemoSupplier extends LogisticsSupplier {

    // 模拟事件发生
    @Transactional
    void mockEvent(long unitId, ShiftStatus status);

}
