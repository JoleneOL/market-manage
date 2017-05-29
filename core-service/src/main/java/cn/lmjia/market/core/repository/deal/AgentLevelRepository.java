package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.util.AbstractAgentLevelRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface AgentLevelRepository extends AbstractAgentLevelRepository<AgentLevel> {

    AgentLevel findTopByLoginAndLevel(Login login, int level);

    List<AgentLevel> findBySystemAndSuperiorNotNull(AgentSystem system);

}
