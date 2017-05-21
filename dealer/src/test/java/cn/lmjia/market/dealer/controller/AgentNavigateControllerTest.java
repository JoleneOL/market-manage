package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.page.AgentManageMainPage;
import cn.lmjia.market.dealer.page.AgentManagePage;
import cn.lmjia.market.dealer.page.AgentOrderManagePage;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
public class AgentNavigateControllerTest extends DealerServiceTest {

    // 访问首页
    @Override
    protected Login allRunWith() {
        return randomLogin(false);
    }

    @Test
    public void agentMain() throws Exception {
        AgentManageMainPage mainPage = mainPage();
        // 然后校验每一个路径
        mainPage.selectMenu("fa-users");
        mainPage.currentContext(AgentManagePage.class);

        mainPage = mainPage();
        mainPage.selectMenu("fa-address-card-o");
        mainPage.currentContext(AgentOrderManagePage.class);
    }

}