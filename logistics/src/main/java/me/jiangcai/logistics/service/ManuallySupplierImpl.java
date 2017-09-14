package me.jiangcai.logistics.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.entity.ManuallyOrder;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.supplier.ManuallySupplier;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class ManuallySupplierImpl implements ManuallySupplier {
    @Override
    public StockShiftUnit makeShift(LogisticsSource source, LogisticsDestination destination
            , Consumer<StockShiftUnit> forUnit, int options) {
        ManuallyOrder manuallyOrder = new ManuallyOrder();
        forUnit.accept(manuallyOrder);
        return manuallyOrder;
    }

    @Override
    public String orderNumberRequireMessage(Locale locale) {
        return "手动订单需要手动录入物流信息";
    }
}
