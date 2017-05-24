package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.service.CommissionRateService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    @Autowired
    private CommissionRateService commissionRateService;

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

        // 先断言提成，并且维护提成


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

        //


    }

}