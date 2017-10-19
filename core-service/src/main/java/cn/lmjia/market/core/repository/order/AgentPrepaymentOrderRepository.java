package cn.lmjia.market.core.repository.order;

import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AgentPrepaymentOrderRepository extends JpaRepository<AgentPrepaymentOrder, Long>
        , JpaSpecificationExecutor<AgentPrepaymentOrder> {
}
