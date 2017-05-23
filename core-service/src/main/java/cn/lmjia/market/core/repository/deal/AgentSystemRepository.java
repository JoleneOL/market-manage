package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.deal.AgentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AgentSystemRepository extends JpaRepository<AgentSystem, Long>, JpaSpecificationExecutor<AgentSystem> {
}
