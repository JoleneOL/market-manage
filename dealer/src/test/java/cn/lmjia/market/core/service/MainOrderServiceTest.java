package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.service.CommissionRateService;
import me.jiangcai.lib.test.matcher.NumberMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    private static final Log log = LogFactory.getLog(MainOrderServiceTest.class);
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
        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);

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
//b
        Login bls[] = new Login[systemService.systemLevel()];
        AgentLevel bs[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(bls, bs);

        // 做单
        int saleIndex = random.nextInt(als.length);
        int recommendIndex = random.nextInt(bls.length);
        MainOrder order = newRandomOrderFor(als[saleIndex], bls[recommendIndex]);

        // 完成支付
        makeOrderPay(order);

        // 调用快速支付
        quickTradeService.makeDone(mainOrderRepository.getOne(order.getId()));

        // 好了开始算了
        AgentLevel addressBoundWinLevel = agentService.addressLevel(order.getInstallAddress());
        if (addressBoundWinLevel != null) {
            log.info("区域奖励获得者:" + addressBoundWinLevel.getLogin().getId());
        } else
            log.info("没有区域奖励获得者");
        // a5应该获得 推荐奖励
        // 其中 0 为sale 1 为 address
        // 2,4,6,8,10 为sale
        // 3,5,7,9,11 为推荐
//        printRates(nowRate);
        for (BigDecimal aNowRate : nowRate) {
            log.info(aNowRate.multiply(BigDecimal.valueOf(100)));
        }
        log.info("销售者:" + saleIndex + ", login:" + als[saleIndex].getId());
        for (int i = 0; i < systemService.systemLevel(); i++) {
            // a0 应该获得奖励是 如果 saleIndex<=0 就是全部的推销奖励
            BigDecimal rate = a(i, saleIndex, nowRate);
            log.info(als[i] + "应该获得的奖励是" + rate);
            assertMoney(order, als[i], rate, addressBoundWinLevel, nowRate[1]);
        }
        for (BigDecimal aNowRate : nowRate) {
            log.info(aNowRate.multiply(BigDecimal.valueOf(100)));
        }
        log.info("推荐者:" + recommendIndex + ", login:" + bls[recommendIndex].getId());
        // 只有推荐上这条线上的人会获得奖励
        for (int i = 0; i < systemService.systemLevel(); i++) {
            BigDecimal rate = b(i, recommendIndex, nowRate, bls);
            log.info(bls[i] + "应该获得的奖励是" + rate);
            assertMoney(order, bls[i], rate, addressBoundWinLevel, nowRate[1]);
        }
//        assertMoney(order, als[4], nowRate[0], addressBoundWinLevel, nowRate[1]);
        // sale 2
        // 0 1

    }

    private BigDecimal b(int index, int recommend, BigDecimal[] rates, Login[] login) {
        // ..|..
        // 只有|以上的人可以获得推荐奖励
        if (index < recommend)
            return rates[index * 2 + 3];
        else if (index == recommend) {
            // 可以获得 index .. max 的奖励
            BigDecimal rate = BigDecimal.ZERO;
            for (int i = index; i < systemService.systemLevel(); i++) {
                rate = rate.add(rates[i * 2 + 3]);
            }
            return rate;
        } else
            return BigDecimal.ZERO;
    }

    private BigDecimal a(int index, int sale, BigDecimal[] rates) {
        // index 0 sale 0 所有奖励+直销
        // index 0 sale 1 0级推销奖励
        // index 1 sale 1 除了0级之外的所有奖励
        // index 0 sale 2 0级推销奖励
        // index 1 sale 2 1级推销奖励
        // index 2 sale 2 除了0,1级之外的所有奖励
        // index 2 sale 3 无奖励

        // 如果 sale <= index 则 index获得直销奖励+index的推销奖励
        if (index > sale) {
            // 其他人做的生意
            return BigDecimal.ZERO;
        } else if (sale == index) {
            BigDecimal rate = rates[0];
            for (int i = sale; i < systemService.systemLevel(); i++) {
                rate = rate.add(rates[i * 2 + 2]);
            }
            return rate;
            // 直销
            // sale-max 推销奖励
        } else {
            // index 推销奖励
            return rates[index * 2 + 2];
        }
    }

    /**
     * 断言login从order获得了rate的奖励
     *
     * @param order
     * @param login
     * @param rate
     * @param addressBoundWinLevel 应该获得区域奖励的等级
     * @param addressRate          区域奖励
     */
    private AbstractBigDecimalAssert<?> assertMoney(MainOrder order, Login login, BigDecimal rate, AgentLevel addressBoundWinLevel, BigDecimal addressRate) throws Exception {
        if (addressBoundWinLevel != null && addressBoundWinLevel.getLogin().equals(login)) {
            rate = rate.add(addressRate);
        }

        String[] types = new String[]{
                "all", "today", "month"
//                , "previous"
                , "quarter"
        };
        String[] noTypes = new String[]{
                "previous"
        };

        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            runWith(login, () -> {
                for (String type : types) {
                    mockMvc.perform(get("/api/commList/{0}", type))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.data.length()").value(NumberMatcher.numberGreatThan(0)));
                }
                for (String type : noTypes) {
                    mockMvc.perform(get("/api/commList/{0}", type))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.data.length()").value(0));
                }
                return null;

            });
        }

        return assertThat(loginService.get(login.getId()).getCommissionBalance())
                .as("身份 %d 应该获得准确的奖励", login.getId())
                .isCloseTo(order.getOrderDueAmount().multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP), Offset.offset(new BigDecimal("0.00001")));
    }

    private void initAgentSystem(Login[] logins, AgentLevel[] levels) {
        for (int i = 0; i < logins.length; i++) {
            if (i == 0) {
                logins[i] = newRandomAgent();
            } else {
                logins[i] = newRandomAgent(levels[i - 1]);
            }
            levels[i] = agentService.getAgent(logins[i], i);
        }
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