package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.aop.BusinessLocker;
import cn.lmjia.market.core.aop.BusinessSafe;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.GoodAdvancePaymentBalanceNotEnoughException;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.repository.order.AgentPrepaymentOrderRepository;
import cn.lmjia.market.core.service.AgentPrepaymentOrderService;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class AgentPrepaymentOrderServiceImpl extends AbstractMainDeliverableOrderService<AgentPrepaymentOrder>
        implements AgentPrepaymentOrderService {

    @Autowired
    private AgentPrepaymentOrderRepository agentPrepaymentOrderRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ReadService readService;

    @Override
    public Class<AgentPrepaymentOrder> getOrderClass() {
        return AgentPrepaymentOrder.class;
    }

    @Override
    public AgentPrepaymentOrder getOrder(long id) {
        return agentPrepaymentOrderRepository.getOne(id);
    }

    @Override
    public AgentPrepaymentOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age
            , Gender gender, Address installAddress, Map<MainGood, Integer> amounts, String mortgageIdentifier)
            throws MainGoodLowStockException {
        return applicationContext.getBean(AgentPrepaymentOrderService.class).newOrder(
                () -> ("agentGoodAdvanceOrder-" + who.getId()).intern(), who, recommendBy, name, mobile
                , age, gender
                , installAddress, amounts, mortgageIdentifier);
    }

    @Override
    @BusinessSafe
    public AgentPrepaymentOrder newOrder(BusinessLocker locker, Login who, Login recommendBy, String name
            , String mobile, int age, Gender gender, Address installAddress, Map<MainGood, Integer> amounts
            , String mortgageIdentifier) throws MainGoodLowStockException {
        return applicationContext.getBean(AgentPrepaymentOrderService.class).newOrder(who, recommendBy, name, mobile, age, gender
                , installAddress, new Amounts(amounts), mortgageIdentifier);
    }

    @Override
    protected AgentPrepaymentOrder persistOrder(AgentPrepaymentOrder order, String mortgageIdentifier) {
        // 在此之前确认下余额是否足够
        final BigDecimal required = order.getOrderDueAmountMoney().getAmount();
        final BigDecimal current = readService.currentGoodAdvancePaymentBalance(order.getBelongs()).getAmount();
        if (current
                .compareTo(required) == -1) {
            throw new GoodAdvancePaymentBalanceNotEnoughException(required, current);
        }
        order.setOrderStatus(OrderStatus.forDeliver);
        return agentPrepaymentOrderRepository.saveAndFlush(order);
    }

    @Override
    protected AgentPrepaymentOrder newOrder(Login who, Login recommendBy) {
        AgentPrepaymentOrder order = new AgentPrepaymentOrder();
        order.setBelongs(who);
        return order;
    }
}
