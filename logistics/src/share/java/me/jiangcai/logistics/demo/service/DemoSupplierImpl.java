package me.jiangcai.logistics.demo.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.demo.DemoSupplier;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class DemoSupplierImpl implements DemoSupplier {

    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public StockShiftUnit makeShift(LogisticsSource source, Collection<? extends Thing> things
            , LogisticsDestination destination, int options, Consumer<StockShiftUnit> consumer) {
        StockShiftUnit unit = new StockShiftUnit();
        consumer.accept(unit);
        return unit;
    }

    @Override
    public void mockEvent(long unitId, ShiftStatus status) {
        applicationEventPublisher.publishEvent(
                new ShiftEvent(
                        stockShiftUnitRepository.getOne(unitId)
                        , status, LocalDateTime.now(), "mock"
                )
        );
    }
}
