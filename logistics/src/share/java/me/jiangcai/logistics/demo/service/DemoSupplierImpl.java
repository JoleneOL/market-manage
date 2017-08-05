package me.jiangcai.logistics.demo.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.StockShiftUnit;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class DemoSupplierImpl implements LogisticsSupplier {
    @Override
    public StockShiftUnit makeDistributionOrder(LogisticsSource source, Collection<? extends Thing> things
            , LogisticsDestination destination, int options, Consumer<StockShiftUnit> consumer) {
        StockShiftUnit unit = new StockShiftUnit();
        consumer.accept(unit);
        return unit;
    }
}
