package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageAgentDetailPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class ManageAgentControllerTest extends ManageServiceTest {

    @Autowired
    private SystemService systemService;
    @Autowired
    private ReadService readService;

    @Test
    public void go() {

        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);

        updateAllRunWith(newRandomManager(ManageLevel.root));
        final AgentLevel toTestAgent = as[as.length - 2];

        // 让他拥有几个直接客户 并且成功下单了！
        addSubUserFor(toTestAgent.getLogin());

        ManageAgentDetailPage page = ManageAgentDetailPage.of(toTestAgent, this, driver);

        page.assertName()
                .isEqualTo(readService.nameForPrincipal(readService.nameForPrincipal(toTestAgent.getLogin())));

        // 点击修改名字，并输入新名字
        final String newName = "新名字" + RandomStringUtils.randomAlphabetic(9);
        page.changeName(newName);
    }

    private void addSubUserFor(Login login) {
        Login newLogin = loginService.newLogin(Login.class, randomMobile(), login, randomEmailAddress());
        MainOrder order = newRandomOrderFor(newLogin, newLogin);
        makeOrderPay(order);
        makeOrderDone(order);
    }

}