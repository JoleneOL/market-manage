package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.DealerServiceTest;
import me.jiangcai.jpa.entity.support.Address;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class AgentServiceTest extends DealerServiceTest {

    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ContactWayService contactWayService;

    @Test
    public void agentSystem() {
        Login login = newRandomAgent(UUID.randomUUID().toString());

        AgentSystem system = agentService.agentSystem(login);
        // 它的下级也是这个熊他那个
        Login sub = newRandomAgent(UUID.randomUUID().toString(), agentService.highestAgent(login));
        assertThat(agentService.agentSystem(sub))
                .isEqualTo(system);
        MainOrder order = newRandomOrderFor(sub, login);

        assertThat(agentService.agentSystem(order.getCustomer().getLogin()))
                .isEqualTo(system);

        // line
        AgentLevel[] line1 = agentService.agentLine(login);
        assertThat(line1)
                .hasSize(systemService.systemLevel());

        AgentLevel[] line2 = agentService.agentLine(sub);
        assertThat(line2)
                .hasSize(systemService.systemLevel());

        AgentLevel[] line3 = agentService.agentLine(order.getCustomer().getLogin());
        assertThat(line3)
                .hasSize(systemService.systemLevel())
                .containsExactly(line2);

        // 地址相关
        final Address address = randomAddress();
        contactWayService.updateAddress(sub, address);
        assertThat(agentService.addressLevel(address).getLogin())
                .isEqualTo(sub);
    }

    @Test
    public void manageable() throws Exception {
        // 管理员可以看到所有的top Agent!
        Manager manager = newRandomManager("", ManageLevel.root);
        String agentName = null;
        long currentTotal = agentService.manageable(manager, agentName, new PageRequest(0, 40)).getTotalElements();
        final int count = random.nextInt(10) + 1;
        int i = count;
        while (i-- > 0)
            newRandomAgent("");

        Page<AgentLevel> fromManager = agentService.manageable(manager, agentName, new PageRequest(0, 40));
//        System.out.println(fromManager.getTotalElements());
        assertThat(fromManager.getTotalElements())
                .isEqualTo(currentTotal + count);

        // 作为一个代理商可以看到下级代理商的情况
        newRandomAgentSystemAnd((login, agentLevel) -> {
            Page<AgentLevel> fromAgent = agentService.manageable(login, agentName, new PageRequest(0, 9999));
            // 里面所有的数据都满足 上级为currentSuper
            assertThat(fromAgent.getContent())
                    .containsOnlyElementsOf(agentsFor(agentLevel));
        });

    }

    private List<AgentLevel> agentsFor(AgentLevel level) {
        return agentLevelRepository.getOne(level.getId()).getSubAgents();
    }

}