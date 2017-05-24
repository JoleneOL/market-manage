package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    @Test
    public void newOrder() throws Exception {
        Login login1 = randomLogin(false);
        Login login2 = randomLogin(false);
        newRandomOrderFor(login1, login2);
        newRandomOrderFor(login1, login2);
        newRandomOrderFor(login1, login2);

        // 设定测试背景
        // A 代理体系
        // A1,A2,A3,A4,A5
        // B 代理体系
        // B1,B2,B3,B4,B5

        // A5 下单购买 推荐人是B代理体系的 B3
        // 则新用户隶属于A 代理体系，同时 A2,A2,A3,A4,A5 以及B1,B2,B3 都将获得提成
        Login a1 = newRandomAgent();
//        agentService.getAgent()

    }

}