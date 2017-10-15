package cn.lmjia.market.core.service;


import cn.lmjia.market.core.aop.MultipleBusinessLocker;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import lombok.Data;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * @param id 订单id
     * @return 获取订单，never null
     */
    @Transactional(readOnly = true)
    MainOrder getOrder(long id);

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

    /**
     * @param orderId 订单号
     * @return 这个订单需要的库存信息
     */
    @Transactional(readOnly = true)
    List<Depot> depotsForOrder(long orderId);

    @EventListener(OrderInstalledEvent.class)
    @Transactional
    MainOrderFinishEvent forOrderInstalledEvent(OrderInstalledEvent event);

    /**
     * @param login 身份
     * @return 该身份下过的所有订单
     */
    @Transactional(readOnly = true)
    List<MainOrder> byOrderBy(Login login);

    // 内部API
    @Data
    class Amounts implements MultipleBusinessLocker {
        private final Map<MainGood, Integer> amounts;

        @Override
        public Object[] toLock() {
            return amounts.keySet().stream()
                    .map(mainGood -> ("MainGoodStockLock-" + mainGood.getProduct().getCode()).intern())
                    .toArray(Object[]::new);
        }
    }
}
