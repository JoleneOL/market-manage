package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.web.page.AgentDetailPage;
import cn.lmjia.market.web.page.AgentManagePage;
import cn.lmjia.market.web.page.ManageMainPage;
import cn.lmjia.market.web.page.WebLoginPage;
import org.junit.Test;
import org.openqa.selenium.WebElement;

/**
 * 管理员登录之后干的事儿
 *
 * @author CJ
 */
public class ManagerFlow extends WebTest {

    @Test
    public void go() throws Exception {
        newRandomAgentSystemAnd(null);

        String rawPassword = randomEmailAddress();
        Manager manager = newRandomManager(rawPassword, ManageLevel.root);

        driver.get("http://localhost/");
        WebLoginPage loginPage = initPage(WebLoginPage.class);
        loginPage.login(manager.getLoginName(), rawPassword);

        // 当前页面是主管理界面
        ManageMainPage manageMainPage = initPage(ManageMainPage.class);

        manageMainPage.selectMenu("fa-users");
//        manageMainPage.printThisPage();
        AgentManagePage agentManagePage = manageMainPage.currentContext(AgentManagePage.class);
        // 点击查看一个 代理
        agentManagePage.buttonsForDetail().max(new RandomComparator())
                .ifPresent(WebElement::click);

        AgentDetailPage agentDetailPage = initPage(AgentDetailPage.class);
        agentDetailPage.printThisPage();
        agentDetailPage.back();
    }
}
