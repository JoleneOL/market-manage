package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.page.AgentManageMainPage;
import cn.lmjia.market.dealer.page.AgentManagePage;
import cn.lmjia.market.dealer.page.AgentOrderManagePage;
import cn.lmjia.market.dealer.page.AgentPlaceOrderPage;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
@Ignore // 暂时忽略该平台
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
        // 可以到下单页面
        AgentOrderManagePage agentOrderManagePage = mainPage.currentContext(AgentOrderManagePage.class);
        String uri = agentOrderManagePage.placeOrderUri();
        // 打开这个地址 即可抵达
        driver.get("http://localhost" + uri);
        System.out.println(driver.getPageSource());
        initPage(AgentPlaceOrderPage.class);

    }

}