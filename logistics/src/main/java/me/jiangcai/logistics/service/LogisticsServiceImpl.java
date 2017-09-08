package me.jiangcai.logistics.service;

import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsHostService;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.OrderDeliveredEvent;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    private static final Log log = LogFactory.getLog(LogisticsServiceImpl.class);
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
    public void mockInstallationEvent(long unitId) {
        StockShiftUnit unit = stockShiftUnitRepository.getOne(unitId);
        applicationEventPublisher.publishEvent(new InstallationEvent(unit, null, null, null, LocalDateTime.now()));
    }

    @Override
    public void forShiftEvent(ShiftEvent event) {
        DeliverableOrder order = getDeliverableOrder(event.getUnit());
        if (order == null) return;
        switch (event.getStatus()) {
            case reject:
                logisticsReject(order);
                break;
            case success:
                logisticsSuccess(event, order);
                break;
            default:
        }
    }

    private DeliverableOrder getDeliverableOrder(StockShiftUnit unit) {
        DeliverableOrder order = applicationContext.getBeansOfType(LogisticsHostService.class).values().stream()
                .map(logisticsHostService -> logisticsHostService.orderFor(unit))
                .filter(Objects::nonNull)
                // 只用第一个？
                .findFirst().orElse(null);
        if (order == null) {
            log.warn("can not find any DeliverableOrder for:" + unit);
            return null;
        }
        return order;
    }

    private void logisticsSuccess(ShiftEvent event, DeliverableOrder order) {
        if (order.updateLogisticsStatus()) {
            applicationEventPublisher.publishEvent(new OrderDeliveredEvent(order, event));
            if (order.updateInstallationStatus(null)) {
                applicationEventPublisher.publishEvent(new OrderInstalledEvent(order, null));
            }
        }
    }

    private void logisticsReject(DeliverableOrder order) {
        order.updateLogisticsStatus();
    }

    @Override
    public void forInstallationEvent(InstallationEvent event) {
        DeliverableOrder order = getDeliverableOrder(event.getUnit());
        if (order == null) return;
        if (order.updateInstallationStatus(event.getUnit())) {
            applicationEventPublisher.publishEvent(new OrderInstalledEvent(order, event));
        }
    }

    @Override
    public StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination) {
        return makeShift(supplier, things, source, destination, 0);
    }

    @Override
    public StockShiftUnit makeShift(LogisticsSupplier supplier1, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination, int options) {
        LogisticsSupplier supplier;
        if (supplier1 == null) {
            supplier = applicationContext.getBean(LogisticsSupplier.class);
        } else
            supplier = supplier1;
        Consumer<StockShiftUnit> consumer = stockShiftUnit -> {
            stockShiftUnit.setInstallation((options & LogisticsOptions.Installation) == LogisticsOptions.Installation);
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
