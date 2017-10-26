package cn.lmjia.market.core.repository.financing;

import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AgentGoodAdvancePaymentRepository extends JpaRepository<AgentGoodAdvancePayment, Long>
        , JpaSpecificationExecutor<AgentGoodAdvancePayment> {
}
