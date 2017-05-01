package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class AgentServiceTest extends DealerServiceTest {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @Test
    public void manageable() throws Exception {
        // 管理员可以看到所有的top Agent!
        Manager manager = newRandomManager("", ManageLevel.root);
        long currentTotal = agentService.manageable(manager, new PageRequest(0, 40)).getTotalElements();
        final int count = random.nextInt(10) + 1;
        int i = count;
        while (i-- > 0)
            newRandomAgent("");

        Page<AgentLevel> fromManager = agentService.manageable(manager, new PageRequest(0, 40));
//        System.out.println(fromManager.getTotalElements());
        assertThat(fromManager.getTotalElements())
                .isEqualTo(currentTotal + count);

        // 作为一个代理商可以看到下级代理商的情况
        newRandomAgentSystemAnd((login, agentLevel) -> {
            Page<AgentLevel> fromAgent = agentService.manageable(login, new PageRequest(0, 9999));
            // 里面所有的数据都满足 上级为currentSuper
            assertThat(fromAgent.getContent())
                    .containsOnlyElementsOf(agentsFor(agentLevel));
        });

    }

    private List<AgentLevel> agentsFor(AgentLevel level) {
        return agentLevelRepository.getOne(level.getId()).getSubAgents();
    }

}