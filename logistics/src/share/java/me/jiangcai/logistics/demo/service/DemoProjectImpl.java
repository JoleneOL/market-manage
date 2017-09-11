package me.jiangcai.logistics.demo.service;

import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.demo.DemoProject;
import me.jiangcai.logistics.demo.entity.DemoOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.OrderDeliveredEvent;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class DemoProjectImpl implements DemoProject {

    private final List<OrderDeliveredEvent> orderDeliveredEventList = Collections.synchronizedList(new ArrayList<>());
    private final List<OrderInstalledEvent> orderInstalledEventList = Collections.synchronizedList(new ArrayList<>());
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private LogisticsService logisticsService;

    @Override
    public DemoOrder createOrder(Map<Product, Integer> amounts) {
        DemoOrder order = new DemoOrder();
        order.setAmounts(amounts);
        entityManager.persist(order);
        return order;
    }

    @Override
    public void cleanEvents() {
        orderDeliveredEventList.clear();
        orderInstalledEventList.clear();
    }

    @Override
    public OrderDeliveredEvent lastOrderDeliveredEvent() {
        if (orderDeliveredEventList.isEmpty())
            return null;
        return orderDeliveredEventList.get(orderDeliveredEventList.size() - 1);
    }

    @Override
    public void forOrderDeliveredEvent(OrderDeliveredEvent event) {
        orderDeliveredEventList.add(event);
    }

    @Override
    public OrderInstalledEvent lastOrderInstalledEvent() {
        if (orderInstalledEventList.isEmpty())
            return null;
        return orderInstalledEventList.get(orderInstalledEventList.size() - 1);
    }

    @Override
    public void forOrderInstalledEvent(OrderInstalledEvent event) {
        orderInstalledEventList.add(event);
    }

    @Override
    public StockShiftUnit work(DemoOrder order, Product product, int integer, boolean testInstall, LogisticsSource source
            , LogisticsDestination destination) {
        DemoOrder demoOrder = entityManager.getReference(DemoOrder.class, order.getId());
        return logisticsService.makeShiftForNormal(null, demoOrder, product, integer, source, destination
                , testInstall ? LogisticsOptions.Installation : 0);
    }

    @Override
    public DeliverableOrder orderFor(StockShiftUnit unit) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DemoOrder> cq = cb.createQuery(DemoOrder.class);
        Root<DemoOrder> root = cq.from(DemoOrder.class);
        try {
            return entityManager.createQuery(cq
                    .where(cb.isMember(unit, root.get("stockShiftUnits")))
            )
                    .getSingleResult();
        } catch (NoResultException ignored) {
//            log.error("居然没有这个订单！我们还做别的生意么?" + unit.getId(), ignored);
            return null;
        }
    }

}
