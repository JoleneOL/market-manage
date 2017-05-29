package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class TeamDataControllerTest extends DealerServiceTest {

    @Autowired
    private SystemService systemService;

    @Test
    public void data() {
        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);
        // 然后每一个人 都增加一个客户
        for (Login login : als) {
            newRandomOrderFor(login, randomLogin(false));
        }

        for (Login login : als) {
            agentService.teamList(login);
        }
    }

}