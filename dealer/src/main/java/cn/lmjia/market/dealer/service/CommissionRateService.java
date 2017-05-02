package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;

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
     * 直接推荐产生的分佣比例
     *
     * @param login 推荐人
     * @return 推荐分佣比例
     */
    BigDecimal recommend(Login login);

}
