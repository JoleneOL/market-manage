package me.jiangcai.logistics.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void mockToStatus(long unitId, ShiftStatus status) {
        StockShiftUnit unit = stockShiftUnitRepository.getOne(unitId);
        applicationEventPublisher.publishEvent(new ShiftEvent(unit, status, LocalDateTime.now(), null, null));
    }

    @Override
    public StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination) {
        return makeShift(supplier, things, source, destination, 0);
    }

    @Override
    public StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination, int options) {
        if (supplier == null) {
            supplier = applicationContext.getBean(LogisticsSupplier.class);
        }
        Consumer<StockShiftUnit> consumer = stockShiftUnit -> {
            stockShiftUnit.setShiftType(ShiftType.logistics);
            stockShiftUnit.setCreateTime(LocalDateTime.now());
            stockShiftUnit.setCurrentStatus(ShiftStatus.init);
            stockShiftUnit.setLastStatusTime(stockShiftUnit.getCreateTime());
            if (source instanceof Depot)
                stockShiftUnit.setOrigin((Depot) source);
            if (destination instanceof Depot) {
                stockShiftUnit.setDestination((Depot) destination);
            }

            stockShiftUnit.setOriginData(source.toDeliverableData());
            stockShiftUnit.setDestinationData(destination.toDeliverableData());

            stockShiftUnit.setAmounts(things.stream()
                    .collect(Collectors.toMap(Thing::getProduct
                            , thing -> new ProductBatch(thing.getProductStatus(), thing.getAmount()))));
        };
        return stockShiftUnitRepository.save(supplier.makeShift(source, destination, consumer, options));
    }

}
