package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 代理有关的财务
 *
 * @author CJ
 */
public interface AgentFinancingService {

    /**
     * 记录进货款
     *
     * @param who    谁
     * @param amount 金额
     * @param type   ..
     * @param serial 单据号
     */
    @Transactional
    void recordGoodPayment(Login who, BigDecimal amount, Integer type, String serial);

    /**
     * 记录代理费
     *
     * @param who    谁
     * @param agent  该代理费是为了
     * @param amount 金额
     * @param type   ..
     * @param serial 单据号
     */
    @Transactional
    void recordAgentFee(Login who, AgentLevel agent, BigDecimal amount, Integer type, String serial);

    /**
     * 拒绝预付货款申请
     *
     * @param manager 执行者
     * @param id      预付货款id
     * @param comment 备注
     */
    @Transactional
    void rejectGoodPayment(Manager manager, long id, String comment);

    /**
     * 同意预付货款申请
     *
     * @param manager 执行者
     * @param id      预付货款id
     * @param comment 备注
     */
    @Transactional
    void approvalGoodPayment(Manager manager, long id, String comment);

    /**
     * 增加一个新的预付货款
     *
     * @param manager 执行者
     * @param login   目标代理商
     * @param amount  金额
     * @param date    日期
     * @param serial  单据号
     */
    @Transactional
    void addGoodPayment(Manager manager, long login, BigDecimal amount, LocalDate date, String serial);
}
