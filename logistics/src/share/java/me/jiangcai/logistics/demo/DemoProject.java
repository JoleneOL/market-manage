package me.jiangcai.logistics.demo;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsHostService;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.demo.entity.DemoOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.OrderDeliveredEvent;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author CJ
 */
public interface DemoProject extends LogisticsHostService {

    @Transactional
    DemoOrder createOrder(Map<Product, Integer> amounts);

    void cleanEvents();

    OrderDeliveredEvent lastOrderDeliveredEvent();

    @EventListener(OrderDeliveredEvent.class)
    void forOrderDeliveredEvent(OrderDeliveredEvent event);

    OrderInstalledEvent lastOrderInstalledEvent();

    @EventListener(OrderInstalledEvent.class)
    void forOrderInstalledEvent(OrderInstalledEvent event);

    @Transactional
    StockShiftUnit work(DemoOrder order, Product product, int integer, boolean testInstall, LogisticsSource source
            , LogisticsDestination destination) throws UnnecessaryShipException;
}
