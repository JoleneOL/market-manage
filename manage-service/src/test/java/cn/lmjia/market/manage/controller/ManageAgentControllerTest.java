package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageAgentDetailPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManageAgentControllerTest extends ManageServiceTest {

    @Autowired
    private SystemService systemService;
    @Autowired
    private ReadService readService;
    @Autowired
    private LoginRepository loginRepository;

    @Test
    public void go() throws InterruptedException {

        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);

        updateAllRunWith(newRandomManager(ManageLevel.root));
        final AgentLevel toTestAgent = as[as.length - 2];

        toTestAgent.getLogin().setGuideUser(als[als.length - 1]);
        loginRepository.save(toTestAgent.getLogin());

        // 让他拥有几个直接客户 并且成功下单了！
        addSubUserFor(toTestAgent.getLogin());

        ManageAgentDetailPage page = ManageAgentDetailPage.of(toTestAgent, this, driver);

        page.assertName()
                .isEqualTo(readService.nameForPrincipal(toTestAgent.getLogin()));

        // 点击修改名字，并输入新名字
        final String newName = "新名字" + RandomStringUtils.randomAlphabetic(9);
        page.changeName(newName);
//        page.printThisPage();
        assertThat(readService.nameForPrincipal(toTestAgent.getLogin()))
                .isEqualTo(newName);

        page.refresh();
        page.assertMobile()
                .isEqualTo(readService.mobileFor(toTestAgent.getLogin()));
        final String newMobile = randomMobile();
        page.changeMobile(newMobile);
        Thread.sleep(1000L);
        assertThat(readService.mobileFor(toTestAgent.getLogin()))
                .isEqualTo(newMobile);
        page.refresh();

        // 修改引导者
        page.assertGuideName()
                .isEqualTo(readService.nameForPrincipal(toTestAgent.getLogin().getGuideUser()));

        // 新增一个Login?
        Login newLogin = newRandomLogin();
        page.changeGuide(newLogin);

        page.assertGuideName()
                .isEqualTo(readService.nameForPrincipal(newLogin));
        assertThat(loginService.get(toTestAgent.getLogin().getId()).getGuideUser())
                .isEqualTo(newLogin);

        //
        Login newLevel = newRandomAgent(toTestAgent.getSuperior().getSuperior());
        page.refresh();
        page.assertSuperiorName()
                .isEqualTo(readService.nameForAgent(toTestAgent.getSuperior()));
        page.changeSuperior(newLevel);
        assertThat(agentService.getAgent(toTestAgent.getId()).getSuperior().getLogin())
                .isEqualTo(newLevel);
    }

    private void addSubUserFor(Login login) {
        Login newLogin = loginService.newLogin(Login.class, randomMobile(), login, randomEmailAddress());
        MainOrder order = newRandomOrderFor(newLogin, newLogin);
        makeOrderPay(order);
        makeOrderDone(order);
    }

}