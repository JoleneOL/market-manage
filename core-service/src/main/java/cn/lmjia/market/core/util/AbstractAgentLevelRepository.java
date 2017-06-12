package cn.lmjia.market.core.util;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * @author CJ
 */
public interface AbstractAgentLevelRepository<T extends AgentLevel>
        extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * 根据身份获取代理信息
     *
     * @param login 登录者
     * @return 代理信息
     */
    List<T> findByLogin(Login login);

    AgentLevel findByLevelAndLoginAndSystem(int level, Login login, AgentSystem system);

    List<T> findBySuperior(AgentLevel agentLevel);

    List<T> findBySuperiorAndSystem(AgentLevel agentLevel, AgentSystem system);

    List<T> findBySystemAndSuperiorIn(AgentSystem system, Collection<AgentLevel> levels);

}
