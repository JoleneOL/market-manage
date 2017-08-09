package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author CJ
 */
public interface LogisticsService {

    /**
     * 尝试模拟一个状态的变化
     *
     * @param unitId 物流订单pk
     * @param status 改变至状态
     */
    @Transactional
    void mockToStatus(long unitId, ShiftStatus status);
    //Distribution resource planning

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     */
    @Transactional
    StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination);

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @param options     选项;{@link LogisticsOptions}
     * @return 配送
     */
    @Transactional
    StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination, int options);

    /**
     * 模拟生成一个安装时间
     *
     * @param unitId 物流id
     */
    @Transactional
    void mockInstallationEvent(long unitId);
}
