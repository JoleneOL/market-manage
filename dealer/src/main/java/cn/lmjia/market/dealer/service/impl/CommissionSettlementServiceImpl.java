package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.deal.pk.OrderCommissionPK;
import cn.lmjia.market.core.entity.support.CommissionType;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.repository.deal.CommissionRepository;
import cn.lmjia.market.core.repository.deal.OrderCommissionRepository;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.CommissionRateService;
import cn.lmjia.market.dealer.service.CommissionSettlementService;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.lib.thread.ThreadSafe;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.event.OrderPaySuccess;
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
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private SystemStringService systemStringService;

    @EventListener(MainOrderFinishEvent.class)
    @ThreadSafe
    @Override
    public void orderFinish(MainOrderFinishEvent event) {
        final MainOrder order = event.getMainOrder();
        OrderCommission orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        if (orderCommission == null) {
            doSettlement(order);
            orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        }

        assert orderCommission != null;
        orderCommission.setPending(false);
    }

    @Override
    @EventListener(OrderPaySuccess.class)
    public void orderPaySuccess(OrderPaySuccess event) {
        final PayableOrder payableOrder = event.getPayableOrder();
        if (payableOrder instanceof MainOrder)
            doSettlement((MainOrder) payableOrder);
    }

    /**
     * 执行结算，如果未结算锅则结算,反之则重新结算
     *
     * @param order 订单
     */
    private void doSettlement(MainOrder order) {
        log.debug("start commission settlement for:" + order);
        OrderCommission orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        if (orderCommission == null)
            orderCommission = new OrderCommission();
        orderCommission.setGenerateTime(LocalDateTime.now());
        orderCommission.setRefund(false);
        orderCommission.setSource(order);

        orderCommission = orderCommissionRepository.save(orderCommission);

        // 给予奖励的目标
        final Login orderBy = mainOrderService.getEnjoyability(order);

        AgentSystem system = agentService.agentSystem(orderBy);
        // 开始分派！
        // 谁可以获得？
        {
            // 销售者
            saveCommission(orderCommission, null, orderBy, commissionRateService.saleRate(system), CommissionType.directMarketing);
        }

        AgentLevel[] sales = agentService.agentLine(orderBy);
        {
            // 以及销售者的代理体系
            for (AgentLevel level : sales) {
                saveCommission(orderCommission, level, level.getLogin(), commissionRateService.directRate(system, level), CommissionType.marketing);
            }
        }

        AgentLevel[] recommends = agentService.recommendAgentLine(orderBy);
        {
            // 推荐者
            // 以及推荐者的代理体系
            for (int i = 0; i < recommends.length; i++) {
                AgentLevel level = recommends[i];
//                if (level != null)
                saveCommission(orderCommission, level, level.getLogin(), commissionRateService.indirectRate(system, i), CommissionType.guideMarketing);
            }
//            for (AgentLevel level : recommends) {
//                if (level != null)
//                    saveCommission(orderCommission, level, level.getLogin(), commissionRateService.indirectRate(system, level), "推荐");
//            }
        }

        if (systemStringService.getCustomSystemString("market.address.reward.enable", null, true, Boolean.class, true)) {
            AgentLevel addressLevel = agentService.addressLevel(order.getInstallAddress());
            if (addressLevel != null) {
                // 以及地域奖励，这个跟系统设定的地址等级有关
                saveCommission(orderCommission, addressLevel, addressLevel.getLogin()
                        , commissionRateService.addressRate(addressLevel), CommissionType.regionService);
            }
        }
    }

    @Override
    public void reSettlement(MainOrder order) {
        if (order.getOrderStatus() == OrderStatus.forPay
                || order.getOrderStatus() == OrderStatus.EMPTY) {
            throw new IllegalArgumentException("无法结算。");
        }
        OrderCommission orderCommission = orderCommissionRepository.findOne(new OrderCommissionPK(order));
        if (orderCommission != null) {
            commissionRepository.deleteByOrderCommission(orderCommission);
        }

        doSettlement(order);
    }

    private void saveCommission(OrderCommission orderCommission, AgentLevel level, Login login, BigDecimal rate
            , CommissionType type) {
        if (rate.equals(BigDecimal.ZERO)) {
            // 0 没有处理的必要
            return;
        }
        Commission commission = new Commission();
        commission.setOrderCommission(orderCommission);
        commission.setAgent(level);
        commission.setWho(login);
        commission.setRate(rate);
        commission.setAmount(orderCommission.getSource().getCommissioningAmount().multiply(rate)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        commission.setType(type);
//        login.setCommissionBalance(login.getCommissionBalance().add(commission.getAmount()));
        commissionRepository.save(commission);
        log.debug("因" + type + " login:" + login.getId() + "获得 提成比:" + rate + "，提成:" + commission.getAmount());
        final SalesAchievement salesAchievement = orderCommission.getSource().getSalesAchievement();
        if (salesAchievement != null) {
            BigDecimal achievementRate = rate.multiply(salesAchievement.getCurrentRate())
                    .setScale(7, BigDecimal.ROUND_DOWN);
            BigDecimal achievementAmount = orderCommission.getSource().getCommissioningAmount().multiply(achievementRate)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            final Login salesmanLogin = salesAchievement.getWhose().getLogin();
            log.debug("但因该订单源自" + salesmanLogin + "的促销，所以分配给TA:" + achievementAmount);
            commission.setRate(rate.subtract(achievementRate));
            commission.setAmount(commission.getAmount().subtract(achievementAmount));
            //
            Commission achievementCommission = new Commission();
            achievementCommission.setOrderCommission(orderCommission);
            achievementCommission.setAgent(level);
            achievementCommission.setWho(salesmanLogin);
            achievementCommission.setRate(achievementRate);
            achievementCommission.setAmount(achievementAmount);
            achievementCommission.setType(CommissionType.sales);
            commissionRepository.save(achievementCommission);
        }
    }
}
