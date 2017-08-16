package me.jiangcai.logistics.demo.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.demo.DemoSupplier;
import me.jiangcai.logistics.entity.StockShiftUnit;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class DemoSupplierImpl implements DemoSupplier {

    @Override
    public StockShiftUnit makeShift(LogisticsSource source,
                                    LogisticsDestination destination, Consumer<StockShiftUnit> forUnit, int options) {
        StockShiftUnit unit = new StockShiftUnit();
        forUnit.accept(unit);
        return unit;
    }

}
