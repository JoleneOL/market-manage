package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.dealer.entity.Commission;
import cn.lmjia.market.dealer.entity.OrderCommission;
import cn.lmjia.market.dealer.entity.pk.OrderCommissionPK;
import cn.lmjia.market.dealer.repository.CommissionRepository;
import cn.lmjia.market.dealer.repository.OrderCommissionRepository;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.CommissionRateService;
import cn.lmjia.market.dealer.service.CommissionSettlementService;
import me.jiangcai.lib.thread.ThreadSafe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Service
public class CommissionSettlementServiceImpl implements CommissionSettlementService {

    private static final Log log = LogFactory.getLog(CommissionSettlementServiceImpl.class);
    @Autowired
    private OrderCommissionRepository orderCommissionRepository;
    @Autowired
    private AgentService agentService;
    @Autowired
    private CommissionRateService commissionRateService;
    @Autowired
    private CommissionRepository commissionRepository;

    @EventListener(MainOrderFinishEvent.class)
    @ThreadSafe
    @Override
    public void orderFinish(MainOrderFinishEvent event) {
        final MainOrder order = event.getMainOrder();
        OrderCommission orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        if (orderCommission != null) {
            throw new IllegalStateException("该订单已结算。");
        }
        doSettlement(order, new OrderCommission());
    }

    private void doSettlement(MainOrder order, OrderCommission orderCommission) {
        log.debug("start commission settlement for:" + order);
//        orderCommission = new OrderCommission();
        orderCommission.setGenerateTime(LocalDateTime.now());
        orderCommission.setRefund(false);
        orderCommission.setSource(order);

        orderCommission = orderCommissionRepository.save(orderCommission);

        AgentSystem system = agentService.agentSystem(order.getOrderBy());
        // 开始分派！
        // 谁可以获得？
        {
            // 销售者
            saveCommission(orderCommission, null, order.getOrderBy(), commissionRateService.saleRate(system), "直销");
        }

        AgentLevel[] sales = agentService.agentLine(order.getOrderBy());
        {
            // 以及销售者的代理体系
            for (AgentLevel level : sales) {
                saveCommission(orderCommission, level, level.getLogin(), commissionRateService.directRate(system, level), "销售");
            }
        }

        AgentLevel[] recommends = agentService.recommendAgentLine(order.getOrderBy());
        {
            // 推荐者
            // 以及推荐者的代理体系
            for (AgentLevel level : recommends) {
                if (level != null)
                    saveCommission(orderCommission, level, level.getLogin(), commissionRateService.indirectRate(system, level), "推荐");
            }
        }

        AgentLevel addressLevel = agentService.addressLevel(order.getInstallAddress());
        if (addressLevel != null) {
            // 以及地域奖励，这个跟系统设定的地址等级有关
            saveCommission(orderCommission, addressLevel, addressLevel.getLogin()
                    , commissionRateService.addressRate(addressLevel), "地域");
        }
    }

    @Override
    public void reSettlement(MainOrder order) {
        OrderCommission orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        if (orderCommission == null) {
            throw new IllegalStateException("该订单尚未结算。");
        }

        commissionRepository.findByOrderCommission(orderCommission)
                .forEach(commission -> {
                    commission.getWho().setCommissionBalance(commission.getWho().getCommissionBalance().subtract(commission.getAmount()));
                    commissionRepository.delete(commission);
                });
//        orderCommissionRepository.delete(orderCommission);

        doSettlement(order, orderCommission);
    }

    private void saveCommission(OrderCommission orderCommission, AgentLevel level, Login login, BigDecimal rate, String message) {
        Commission commission = new Commission();
        commission.setOrderCommission(orderCommission);
        commission.setAgent(level);
        commission.setWho(login);
        commission.setRate(rate);
        commission.setAmount(orderCommission.getSource().getCommissioningAmount().multiply(rate)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        login.setCommissionBalance(login.getCommissionBalance().add(commission.getAmount()));
        commissionRepository.save(commission);
        log.debug("因" + message + " login:" + login.getId() + "获得 提成比:" + rate + "，提成:" + commission.getAmount());
    }
}
