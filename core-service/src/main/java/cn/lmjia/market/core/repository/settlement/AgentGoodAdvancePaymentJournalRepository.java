package cn.lmjia.market.core.repository.settlement;

import cn.lmjia.market.core.entity.settlement.AgentGoodAdvancePaymentJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AgentGoodAdvancePaymentJournalRepository extends JpaRepository<AgentGoodAdvancePaymentJournal, String>
        , JpaSpecificationExecutor<AgentGoodAdvancePaymentJournal> {
}
