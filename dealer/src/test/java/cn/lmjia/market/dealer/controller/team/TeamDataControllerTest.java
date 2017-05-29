package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.repository.cache.LoginRelationRepository;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

/**
 * @author CJ
 */
public class TeamDataControllerTest extends DealerServiceTest {

    @Autowired
    private SystemService systemService;
    @Autowired
    private LoginRelationRepository loginRelationRepository;

    @Test
    public void data() {
        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);
        // 然后每一个人 都增加一个客户
        for (Login login : als) {
            newRandomOrderFor(login, randomLogin(false));
        }

        loginRelationRepository.findBySystem(as[0].getSystem())
                .stream()
                .sorted(new Comparator<LoginRelation>() {
                    @Override
                    public int compare(LoginRelation o1, LoginRelation o2) {
                        return (int) ((o1.getFrom().getId() - o2.getFrom().getId()) * 1000000
                                + (o1.getTo().getId() - o2.getTo().getId()) * 1000
                                + o1.getLevel() - o2.getLevel());
                    }
                })
                .forEach(System.out::println);

    }

}