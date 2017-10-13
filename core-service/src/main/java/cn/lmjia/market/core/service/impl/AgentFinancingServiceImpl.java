package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.financing.AgentFeeRecord;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import cn.lmjia.market.core.entity.financing.AgentGoodPaymentRecord;
import cn.lmjia.market.core.entity.financing.AgentIncomeRecord;
import cn.lmjia.market.core.repository.financing.AgentFeeRecordRepository;
import cn.lmjia.market.core.repository.financing.AgentGoodAdvancePaymentRepository;
import cn.lmjia.market.core.repository.financing.AgentGoodPaymentRecordRepository;
import cn.lmjia.market.core.service.AgentFinancingService;
import cn.lmjia.market.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private AgentGoodAdvancePaymentRepository agentGoodAdvancePaymentRepository;
    @Autowired
    private LoginService loginService;

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

    @Override
    public void rejectGoodPayment(Manager manager, long id, String comment) {
        AgentGoodAdvancePayment payment = agentGoodAdvancePaymentRepository.getOne(id);
        payment.setApproval(manager);
        payment.setApproved(false);
        payment.setApprovalTime(LocalDateTime.now());
        payment.setComment(comment);
    }

    @Override
    public void approvalGoodPayment(Manager manager, long id, String comment) {
        AgentGoodAdvancePayment payment = agentGoodAdvancePaymentRepository.getOne(id);
        payment.setApproval(manager);
        payment.setApproved(true);
        payment.setApprovalTime(LocalDateTime.now());
        payment.setComment(comment);
    }

    @Override
    public void addGoodPayment(Manager manager, long login, BigDecimal amount, LocalDate date, String serial) {
        AgentGoodAdvancePayment payment = new AgentGoodAdvancePayment();
        payment.setOperator(manager);
        payment.setLogin(loginService.get(login));
        payment.setHappenTime(date.atStartOfDay());
        payment.setAmount(amount);
        payment.setSerial(serial);
        agentGoodAdvancePaymentRepository.save(payment);
    }
}
