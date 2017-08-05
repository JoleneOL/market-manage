package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.StockShiftUnit;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 物流服务供应商
 *
 * @author CJ
 */
public interface LogisticsSupplier {

    /**
     * 供应商创建物流订单
     *
     * @param source      来源，如果来源是一个{@link me.jiangcai.logistics.entity.Depot}则创建出库单
     * @param things      相关
     * @param destination
     * @param options
     * @param consumer
     * @return
     */
    StockShiftUnit makeDistributionOrder(LogisticsSource source, Collection<? extends Thing> things
            , LogisticsDestination destination, int options, Consumer<StockShiftUnit> consumer);
}
