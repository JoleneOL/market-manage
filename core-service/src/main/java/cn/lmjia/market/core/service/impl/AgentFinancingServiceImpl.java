package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.financing.AgentFeeRecord;
import cn.lmjia.market.core.entity.financing.AgentGoodPaymentRecord;
import cn.lmjia.market.core.entity.financing.AgentIncomeRecord;
import cn.lmjia.market.core.repository.financing.AgentFeeRecordRepository;
import cn.lmjia.market.core.repository.financing.AgentGoodPaymentRecordRepository;
import cn.lmjia.market.core.service.AgentFinancingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Service
public class AgentFinancingServiceImpl implements AgentFinancingService {

    @Autowired
    private AgentGoodPaymentRecordRepository agentGoodPaymentRecordRepository;
    @Autowired
    private AgentFeeRecordRepository agentFeeRecordRepository;

    @Override
    public void recordGoodPayment(Login who, BigDecimal amount, Integer type, String serial) {
        AgentGoodPaymentRecord record = new AgentGoodPaymentRecord();
        forAgentRecord(record, who, amount, type, serial);
        agentGoodPaymentRecordRepository.save(record);
    }

    private void forAgentRecord(AgentIncomeRecord record, Login who, BigDecimal amount, Integer type, String serial) {
        record.setAmount(amount);
        record.setHappenTime(LocalDateTime.now());
        record.setSerial(serial);
        record.setType(type);
        record.setWho(who);
    }

    @Override
    public void recordAgentFee(Login who, AgentLevel agent, BigDecimal amount, Integer type, String serial) {
        AgentFeeRecord record = new AgentFeeRecord();
        forAgentRecord(record, who, amount, type, serial);
        record.setAgent(agent);
        agentFeeRecordRepository.save(record);
    }
}
