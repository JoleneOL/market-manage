package cn.lmjia.market.core.repository.financing;

import cn.lmjia.market.core.entity.financing.AgentFeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AgentFeeRecordRepository extends JpaRepository<AgentFeeRecord, Long>, JpaSpecificationExecutor<AgentFeeRecord> {
}
