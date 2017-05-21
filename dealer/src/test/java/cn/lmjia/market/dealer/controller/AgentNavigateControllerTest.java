package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;

/**
 * @author CJ
 */
public class AgentNavigateControllerTest extends DealerServiceTest {

    // 访问首页

    @Test
    public void agentMain() throws Exception {
        runWith(randomLogin(false), () -> {

            return null;
        });
    }

}