package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.CommissionRateService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Service("commissionRateService")
public class CommissionRateServiceImpl implements CommissionRateService {

    @Autowired
    private AgentService agentService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private SystemService systemService;

    @Override
    public BigDecimal directRate(AgentLevel agent) {
        return rate(agent, true);
    }

    private BigDecimal rate(AgentLevel agent, boolean direct) {
        int level = agentService.agentLevel(agent);
        return systemStringService
                .getSystemString("commission.rate." + (direct ? "direct" : "indirect") + ".l" + level
                        , BigDecimal.class, defaultRate(level, direct));
    }

    private BigDecimal defaultRate(int level, boolean direct) {
        switch (level) {
            case 0:
                return BigDecimal.ZERO;
            case 1:
                return BigDecimal.ZERO;
            case 2:
            case 3:
                return direct ? new BigDecimal("0.4") : new BigDecimal("0.1");
            case 4:
                return direct ? new BigDecimal("0.8") : new BigDecimal("0.2");
            default:
                throw new IllegalStateException("超过预期" + systemService.systemLevel() + "的级别");
        }
    }

    @Override
    public BigDecimal indirectRate(AgentLevel agent) {
        return rate(agent, false);
    }

    @Override
    public BigDecimal recommend(Login login) {
        return systemStringService.getSystemString("commission.rate.recommend"
                , BigDecimal.class, new BigDecimal("0.2"));
    }
}
