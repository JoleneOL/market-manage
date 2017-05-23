package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.deal.AgentRate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统服务；它不依赖任何玩意儿
 *
 * @author CJ
 */
public interface SystemService {
    /**
     * 我的URI
     */
    String wechatMyURi = "/wechatMy";
    /**
     * 我的团队URI
     */
    String wechatMyTeamURi = "/wechatMyTeam";
    /**
     * 下单URI
     */
    String wechatOrderURi = "/wechatOrder";

    /**
     * @return 代理体系的层次数量
     */
    default int systemLevel() {
        return 5;
    }

    /**
     * @return 默认新代理体系的代理奖励层次
     */
    default Map<Integer, AgentRate> defaultAgentRates() {
        Map<Integer, AgentRate> data = new HashMap<>();
        data.put(0, new AgentRate(BigDecimal.ZERO, BigDecimal.ZERO));
        data.put(1, new AgentRate(BigDecimal.ZERO, BigDecimal.ZERO));
        data.put(2, new AgentRate(new BigDecimal("0.04"), new BigDecimal("0.01")));
        data.put(3, new AgentRate(new BigDecimal("0.04"), new BigDecimal("0.01")));
        data.put(4, new AgentRate(new BigDecimal("0.08"), new BigDecimal("0.02")));
        return data;
    }

    /**
     * @return 默认新代理体系的直销奖励
     */
    default AgentRate defaultOrderRate() {
        return new AgentRate(new BigDecimal("0.2"), BigDecimal.ZERO);
    }

    /**
     * @param uri 传入uri通常/开头
     * @return 完整路径
     */
    String toUrl(String uri);
}
