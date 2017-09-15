package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.StockShiftUnit;

import java.util.Locale;
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
     * @param destination 目的，如果目的是一个{@link me.jiangcai.logistics.entity.Depot}则创建入库单
     * @param options     额外选项
     * @return 新的库存变化单
     */
    StockShiftUnit makeShift(LogisticsSource source,
                             LogisticsDestination destination, Consumer<StockShiftUnit> forUnit, int options);

    /**
     * @param locale 用户语言偏好
     * @return 是否需要输入订单号，null表示不需要；返回信息是提示输入订单号的描述
     */
    default String orderNumberRequireMessage(Locale locale) {
        return null;
    }
}
