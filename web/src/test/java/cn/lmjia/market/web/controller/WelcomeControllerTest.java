package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.dealer.page.AgentManageMainPage;
import cn.lmjia.market.web.page.WebLoginPage;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author CJ
 */
@Ignore
public class WelcomeControllerTest extends WebTest {

    @Test
    public void index() throws Exception {
        // 管理员登录
        String rawPassword = randomEmailAddress();
        Manager manager = newRandomManager(rawPassword, ManageLevel.root);

        driver.get("http://localhost/");
        WebLoginPage loginPage = initPage(WebLoginPage.class);
        loginPage.login(manager.getLoginName(), rawPassword);

        // 当前页面是主管理界面
        initPage(AgentManageMainPage.class);

        createWebDriver();
        Login login = newRandomAgent(rawPassword);

        driver.get("http://localhost/");
        loginPage = initPage(WebLoginPage.class);
        loginPage.login(login.getLoginName(), rawPassword);

        // 当前页面是主管理界面
        initPage(AgentManageMainPage.class);

        // 代理商登录
    }

}