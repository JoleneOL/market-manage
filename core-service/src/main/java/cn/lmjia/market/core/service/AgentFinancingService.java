package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

}
