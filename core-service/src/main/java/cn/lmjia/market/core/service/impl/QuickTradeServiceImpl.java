package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.QuickTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class QuickTradeServiceImpl implements QuickTradeService {
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void makeDone(MainOrder inputOrder) {
        MainOrder order = mainOrderRepository.getOne(inputOrder.getId());
        if (!order.isPay()) {
            throw new IllegalArgumentException("该订单尚未支付，无法直接完成。");
        }
        if (order.getOrderStatus() == OrderStatus.afterSale) {
            throw new IllegalArgumentException("该订单已完成。");
        }
        if (order.getOrderStatus() != OrderStatus.afterSale) {
            order.setOrderStatus(OrderStatus.afterSale);
//            order = mainOrderRepository.save(order);
        }

        applicationEventPublisher.publishEvent(new MainOrderFinishEvent(order, null));
    }
}
