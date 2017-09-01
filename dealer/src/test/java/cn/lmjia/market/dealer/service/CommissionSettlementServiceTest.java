package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.data.Offset;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 佣金结算的测试
 * 推荐奖励并非来自订单的推荐者；而是使用根据下单者（的推荐者）
 * 测试模型是一个
 * A推荐B,B推荐C,C推荐D..
 *
 * @author CJ
 */
@Ignore
public class CommissionSettlementServiceTest extends DealerServiceTest {

    private static final Log log = LogFactory.getLog(CommissionSettlementServiceTest.class);
    @Autowired
    private CommissionRateService commissionRateService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private ReadService readService;
    @Autowired
    private CommissionSettlementService commissionSettlementService;

    private void hasDefaultRate(AgentLevel level) {
        assertThat(commissionRateService.directRate(level))
                .isEqualTo(systemService.defaultAgentRates().get(level.getLevel()).getMarketRate());
        assertThat(commissionRateService.indirectRate(level))
                .isEqualTo(systemService.defaultAgentRates().get(level.getLevel()).getRecommendRate());
    }

    private BigDecimal f(BigDecimal total, int i) {
        if (i == 1)
            return total.setScale(3, BigDecimal.ROUND_HALF_UP);
        BigDecimal rate = BigDecimal.valueOf(0.1D + 1.8D * Math.abs(random.nextDouble()));
        return total.divide(BigDecimal.valueOf(i), BigDecimal.ROUND_HALF_UP).multiply(rate).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @return 将切成count份
     */
    private BigDecimal[] randomCute(BigDecimal all, int count) {
        BigDecimal[] data = new BigDecimal[count];
        // f(剩余量,剩余次数)
        // 剩余次数==1 > 剩余量
        // 剩余量/剩余次数*0.1-1.9
        for (int i = 0; i < data.length; i++) {
            BigDecimal total = BigDecimal.ZERO;
            for (int j = 0; j < i; j++) {
                total = total.add(data[j]);
            }
            total = all.subtract(total);
            data[i] = f(total, count - i);
        }
        return data;
    }

    @Test
    public void go() {

        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystemWithRecommend(als, as);

        AgentSystem a = as[0].getSystem();

        // 先断言提成，并且维护提成

        // 首先是断言为默认提成
        assertThat(commissionRateService.saleRate(a))
                .isEqualTo(systemService.defaultOrderRate());
        assertThat(commissionRateService.addressRate(as[0]))
                .isEqualTo(systemService.defaultAddressRate());

        for (AgentLevel a1 : as) {
            hasDefaultRate(a1);
        }

        // 再应用新的提成
        // 将0.4 - 0.8 分给诸位
        BigDecimal all = BigDecimal.valueOf(0.4D + Math.abs(random.nextDouble()) * 0.4D);
        // 一共分为 10 + 2
        BigDecimal[] nowRate = randomCute(all, systemService.systemLevel() * 2 + 2);

        // 其中 0 为sale 1 为 address
        // 2,4,6,8,10 为sale
        // 3,5,7,9,11 为推荐
        commissionRateService.updateSaleRate(a, nowRate[0]);
        commissionRateService.updateAddressRate(a, nowRate[1]);
        for (int i = 0; i < systemService.systemLevel(); i++) {
            commissionRateService.updateDirectRate(a, i, nowRate[i * 2 + 2]);
            commissionRateService.updateIndirectRate(a, i, nowRate[i * 2 + 3]);
        }

        //
        for (int i = 0; i < als.length; i++) {
            // 抓取当前余额
            BigDecimal[] currentBalances = currentBalances(als);
            MainOrder order = newRandomOrderFor(als[i], randomLogin(false));

            makeOrderPay(order);
            makeOrderDone(order);

            // 好了开始算了
            AgentLevel addressBoundWinLevel = agentService.addressLevel(order.getInstallAddress());
            if (addressBoundWinLevel != null) {
                log.info("区域奖励获得者:" + addressBoundWinLevel.getLogin().getId());
            } else
                log.info("没有区域奖励获得者");

            //
            // 最高等级越高则 其推荐者 越少
            // 0 则 0 个
            // 1 则 一个 推荐者是 0
            // 2 则 2个 推荐者分别是 0,1
            for (int j = 0; j < currentBalances.length; j++) {
                // 直接销售奖励
                if (i == j) {
                    // 销售奖励给予
                    currentBalances[j] = currentBalances[j].add(order.getCommissioningAmount().multiply(nowRate[0]));
                }
                // 区域销售奖励
                if (addressBoundWinLevel != null && addressBoundWinLevel.getLogin().equals(als[j])) {
                    currentBalances[j] = currentBalances[j].add(order.getCommissioningAmount().multiply(nowRate[1]));
                }
                // 内部奖励
                BigDecimal decimal = order.getCommissioningAmount().multiply(nowRate[j * 2 + 2]);
                // 给谁？
                currentBalances[Math.min(i, j)] = currentBalances[Math.min(i, j)].add(decimal);
                // 间接奖励
                decimal = order.getCommissioningAmount().multiply(nowRate[j * 2 + 3]);
                if (i > j) {
                    // 只有这种情况才有
                    currentBalances[j] = currentBalances[j].add(decimal);
                }
            }
            assertCurrentBalances(als, currentBalances);
            // 重新结算也一样
            commissionSettlementService.reSettlement(mainOrderRepository.getOne(order.getId()));
            assertCurrentBalances(als, currentBalances);
        }

    }

    private void assertCurrentBalances(Login[] logins, BigDecimal[] balances) {
        for (int i = 0; i < logins.length; i++) {
            assertThat(readService.currentBalance(logins[i]).getAmount())
                    .as(logins[i] + "的余额应该是期望的值")
                    .isCloseTo(balances[i], Offset.offset(new BigDecimal("0.001")));
        }
    }

    private BigDecimal[] currentBalances(Login[] logins) {
        BigDecimal[] values = new BigDecimal[logins.length];
        for (int i = 0; i < logins.length; i++) {
            values[i] = readService.currentBalance(logins[i]).getAmount();
        }
        return values;
    }

}