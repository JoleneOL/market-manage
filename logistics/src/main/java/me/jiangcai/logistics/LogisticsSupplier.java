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
     * @param things      相关货物
     * @param destination 目的，如果目的是一个{@link me.jiangcai.logistics.entity.Depot}则创建入库单
     * @param options     额外选项
     * @param consumer    初始化库存变化单之后需要立即执行的内容
     * @return 新的库存变化单
     */
    StockShiftUnit makeShift(LogisticsSource source, Collection<? extends Thing> things
            , LogisticsDestination destination, int options, Consumer<StockShiftUnit> consumer);
}
