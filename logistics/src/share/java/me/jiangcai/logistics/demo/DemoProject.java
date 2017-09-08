package me.jiangcai.logistics.demo;

import me.jiangcai.logistics.demo.entity.DemoOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.OrderDeliveredEvent;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author CJ
 */
public interface DemoProject {

    @Transactional
    DemoOrder createOrder(Map<Product, Integer> amounts);

    @Transactional
    StockShiftUnit makeShift(DemoOrder order, Product product, int amount, boolean install);

    void cleanEvents();

    OrderDeliveredEvent lastOrderDeliveredEvent();

    @EventListener(OrderDeliveredEvent.class)
    void forOrderDeliveredEvent(OrderDeliveredEvent event);

    OrderInstalledEvent lastOrderInstalledEvent();

    @EventListener(OrderInstalledEvent.class)
    void forOrderInstalledEvent(OrderInstalledEvent event);
}
