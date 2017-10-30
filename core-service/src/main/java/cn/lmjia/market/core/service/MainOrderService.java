package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
public interface MainOrderService extends MainDeliverableOrderService<MainOrder> {

    /**
     * 给所有未支付的订单添加 Executor，如果想  <strong>market.core.service.order.maxMinuteForPay</strong> 实时生效，可以调这个方法
     */
    @Transactional
    void createExecutorToForPayOrder();

    /**
     * @return 所有订单
     */
    @Transactional(readOnly = true)
    List<MainOrder> allOrders();

    /**
     * @param orderId {@link MainOrder#getSerialId(Path, CriteriaBuilder)}
     * @return 获取订单，never null
     */
    @Transactional(readOnly = true)
    MainOrder getOrder(String orderId);

    /**
     * @param id 订单id
     * @return 订单是否已支付
     */
    @Transactional(readOnly = true)
    boolean isPaySuccess(long id);

    /**
     * @param order 订单
     * @return 享受该订单受益者
     */
    @Transactional(readOnly = true)
    Login getEnjoyability(MainOrder order);

    /**
     * @param orderBy 下单人
     * @return 如果该人下单则何人获得收益
     */
    @Transactional(readOnly = true)
    Login getEnjoyability(Login orderBy);

    @Transactional
    void updateOrderTime(LocalDateTime time);

    @EventListener(OrderInstalledEvent.class)
    @Transactional
    MainOrderFinishEvent forOrderInstalledEvent(OrderInstalledEvent event);

    /**
     * @param login 身份
     * @return 该身份下过的所有订单
     */
    @Transactional(readOnly = true)
    List<MainOrder> byOrderBy(Login login);

}
