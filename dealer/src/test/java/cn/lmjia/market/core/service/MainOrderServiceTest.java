package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.service.CommissionRateService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    @Autowired
    private CommissionRateService commissionRateService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private QuickTradeService quickTradeService;

    @Test
    public void newOrder() throws Exception {
//        Login login1 = randomLogin(false);
//        Login login2 = randomLogin(false);
//        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);

        // 设定测试背景
        // A 代理体系
        // A1,A2,A3,A4,A5
        // B 代理体系
        // B1,B2,B3,B4,B5

        // A5 下单购买 推荐人是B代理体系的 B3
        // 则新用户隶属于A 代理体系，同时 A2,A2,A3,A4,A5 以及B1,B2,B3 都将获得提成


        Login a1l = newRandomAgent();
        AgentLevel a1 = agentService.getAgent(a1l, 0);
        Login a2l = newRandomAgent(a1);
        AgentLevel a2 = agentService.getAgent(a2l, 1);
        Login a3l = newRandomAgent(a2);
        AgentLevel a3 = agentService.getAgent(a3l, 2);
        Login a4l = newRandomAgent(a3);
        AgentLevel a4 = agentService.getAgent(a4l, 3);
        Login a5l = newRandomAgent(a4);
        AgentLevel a5 = agentService.getAgent(a5l, 4);
        AgentSystem a = a1.getSystem();

        // 先断言提成，并且维护提成

        // 首先是断言为默认提成
        assertThat(commissionRateService.saleRate(a))
                .isEqualTo(systemService.defaultOrderRate());
        assertThat(commissionRateService.addressRate(a1))
                .isEqualTo(systemService.defaultAddressRate());
        hasDefaultRate(a1);
        hasDefaultRate(a2);
        hasDefaultRate(a3);
        hasDefaultRate(a4);
        hasDefaultRate(a5);

        // 再应用新的提成
        // 将0.4 - 0.8 分给诸位
        BigDecimal all = BigDecimal.valueOf(0.4D + Math.abs(random.nextDouble()) * 0.4D);
        // 一共分为 10 + 2
        BigDecimal[] nowRate = randomCute(all, systemService.systemLevel() * 2 + 2);
        for (int i = 0; i < nowRate.length; i++) {
            System.out.println(nowRate[i].multiply(BigDecimal.valueOf(100)));
        }
        // 其中 0 为sale 1 为 address
        // 2,4,6,8,10 为sale
        // 3,5,7,9,11 为推荐
        commissionRateService.updateSaleRate(a, nowRate[0]);
        commissionRateService.updateAddressRate(a, nowRate[1]);
        for (int i = 0; i < systemService.systemLevel(); i++) {
            commissionRateService.updateDirectRate(a, i, nowRate[i * 2 + 2]);
            commissionRateService.updateIndirectRate(a, i, nowRate[i * 2 + 3]);
        }
//b
        Login b1l = newRandomAgent();
        AgentLevel b1 = agentService.getAgent(b1l, 0);
        Login b2l = newRandomAgent(b1);
        AgentLevel b2 = agentService.getAgent(b2l, 1);
        Login b3l = newRandomAgent(b2);
        AgentLevel b3 = agentService.getAgent(b3l, 2);
        Login b4l = newRandomAgent(b3);
        AgentLevel b4 = agentService.getAgent(b4l, 3);
        Login b5l = newRandomAgent(b4);
        AgentLevel b5 = agentService.getAgent(b5l, 4);

        // 做单
        MainOrder order = newRandomOrderFor(a5l, b3l);

        // 完成支付
        makeOrderPay(order);

        // 调用快速支付
        quickTradeService.makeDone(mainOrderRepository.getOne(order.getId()));

        // 好了开始算了


    }

    /**
     * @return 将切成count份
     */
    private BigDecimal[] randomCute(BigDecimal all, int count) {
        BigDecimal[] data = new BigDecimal[count];
        // F(剩余量,剩余次数)
        // 剩余次数==1 > 剩余量
        // 剩余量/剩余次数*0.1-1.9
        for (int i = 0; i < data.length; i++) {
            BigDecimal total = BigDecimal.ZERO;
            for (int j = 0; j < i; j++) {
                total = total.add(data[j]);
            }
            total = all.subtract(total);
            data[i] = F(total, count - i);
        }
        return data;
    }

    private BigDecimal F(BigDecimal total, int i) {
        if (i == 1)
            return total.setScale(3, BigDecimal.ROUND_HALF_UP);
        BigDecimal rate = BigDecimal.valueOf(0.1D + 1.8D * Math.abs(random.nextDouble()));
        return total.divide(BigDecimal.valueOf(i), BigDecimal.ROUND_HALF_UP).multiply(rate).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    private void hasDefaultRate(AgentLevel level) {
        assertThat(commissionRateService.directRate(level))
                .isEqualTo(systemService.defaultAgentRates().get(level.getLevel()).getMarketRate());
        assertThat(commissionRateService.indirectRate(level))
                .isEqualTo(systemService.defaultAgentRates().get(level.getLevel()).getRecommendRate());
    }

}