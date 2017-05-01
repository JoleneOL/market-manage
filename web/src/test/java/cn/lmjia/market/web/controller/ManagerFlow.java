package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.web.page.AgentManagePage;
import cn.lmjia.market.web.page.ManageMainPage;
import cn.lmjia.market.web.page.WebLoginPage;
import org.junit.Test;

/**
 * 管理员登录之后干的事儿
 *
 * @author CJ
 */
public class ManagerFlow extends WebTest {

    @Test
    public void go() {
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
    }
}
