package me.jiangcai.logistics.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.supplier.ManuallySupplier;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class ManuallySupplierImpl implements ManuallySupplier {
    @Override
    public StockShiftUnit makeShift(LogisticsSource source, LogisticsDestination destination
            , Consumer<StockShiftUnit> forUnit, int options) {
        return null;
    }
}
