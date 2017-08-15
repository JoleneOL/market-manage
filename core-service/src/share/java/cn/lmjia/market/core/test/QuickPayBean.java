package cn.lmjia.market.core.test;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.repository.MainOrderRepository;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
@Service
public class QuickPayBean {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private MainOrderRepository mainOrderRepository;

    @Transactional
    public void makePay(MainOrder orderInput) {
        MainOrder order = mainOrderRepository.getOne(orderInput.getId());
        applicationEventPublisher.publishEvent(new OrderPaySuccess(order, null));
    }

}
