package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.web.page.ManageMainPage;
import cn.lmjia.market.web.page.WebLoginPage;
import me.jiangcai.lib.seext.EnumUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class WelcomeControllerTest extends WebTest {
    @Autowired
    private LoginService loginService;
    @Autowired
    private AgentService agentService;

    @Test
    public void index() throws Exception {
        // 管理员登录
        String rawPassword = randomEmailAddress();
        Manager manager = newRandomManager(rawPassword, ManageLevel.root);

        driver.get("http://localhost/");
        WebLoginPage loginPage = initPage(WebLoginPage.class);
        loginPage.login(manager.getLoginName(), rawPassword);

        // 当前页面是主管理界面
        initPage(ManageMainPage.class);

        createWebDriver();
        Login login = newRandomAgent(rawPassword);

        driver.get("http://localhost/");
        loginPage = initPage(WebLoginPage.class);
        loginPage.login(login.getLoginName(), rawPassword);

        // 当前页面是主管理界面
        initPage(ManageMainPage.class);

        // 代理商登录
    }

    /**
     * 新增并且保存一个随机的代理商
     *
     * @param rawPassword 明文密码
     * @return 已保存的代理商的登录
     */
    private Login newRandomAgent(String rawPassword) {
        Login login = new Login();
        login.setLoginName(randomMobile());
        login = loginService.password(login, rawPassword);
        agentService.addTopAgent(login, "随机代理" + RandomStringUtils.randomAlphabetic(4));
        return login;
    }

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param rawPassword 明文密码
     * @param levels      等级;可以为null
     * @return 已保存的管理员
     */
    private Manager newRandomManager(String rawPassword, ManageLevel... levels) {
        Manager manager = new Manager();
        manager.setLoginName(randomMobile());
        manager.setLevel(EnumUtils.randomEnum(ManageLevel.class, levels));
        return loginService.password(manager, rawPassword);
    }

}