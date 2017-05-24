package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;

import java.math.BigDecimal;

/**
 * 佣金比例服务
 *
 * @author CJ
 */
public interface CommissionRateService {

    /**
     * 这个代理体系内产生订单获得的分佣比例
     *
     * @param agent 代理
     * @return 直接分佣比例
     */
    BigDecimal directRate(AgentLevel agent);

    /**
     * 「代理」所引导的代理体系产生的订单给予「代理」的分佣奖励
     *
     * @param agent 代理
     * @return 间接分佣比例
     */
    BigDecimal indirectRate(AgentLevel agent);

    /**
     * 销售产生的奖励
     *
     * @param system 销售系统
     * @return 分佣比例
     */
    BigDecimal saleRate(AgentSystem system);

    /**
     * 这个代理体系内这个代理产生订单获得的分佣比例
     *
     * @param agent  代理
     * @param system 代理体系
     * @return 直接分佣比例
     */
    BigDecimal directRate(AgentSystem system, AgentLevel agent);

    /**
     * 「代理」所引导的代理体系产生的订单给予「代理」的分佣奖励
     *
     * @param agent  代理
     * @param system 代理体系
     * @return 间接分佣比例
     */
    BigDecimal indirectRate(AgentSystem system, AgentLevel agent);

    /**
     * @param agent 代理
     * @return 区域奖励提成
     */
    BigDecimal addressRate(AgentLevel agent);
}
