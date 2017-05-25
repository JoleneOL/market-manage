package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentRate;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.deal.AgentSystemRepository;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.CommissionRateService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

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
    @Autowired
    private AgentSystemRepository agentSystemRepository;

    @Override
    public BigDecimal directRate(AgentLevel agent) {
        return directRate(agent.getSystem(), agent);
    }

    @Override
    public BigDecimal indirectRate(AgentLevel agent) {
        return indirectRate(agent.getSystem(), agent);
    }

    @Override
    public BigDecimal saleRate(AgentSystem system) {
        if (system.getOrderRate() == null)
            return systemService.defaultOrderRate();
        return system.getOrderRate();
    }

    @Override
    public BigDecimal addressRate(AgentLevel agent) {
        if (agent.getSystem().getAddressRate() == null)
            return systemService.defaultAddressRate();
        return agent.getSystem().getAddressRate();
    }

    @Override
    public void updateSaleRate(AgentSystem system, BigDecimal rate) {
        system.setOrderRate(rate);
        agentSystemRepository.save(system);
    }

    @Override
    public void updateAddressRate(AgentSystem system, BigDecimal rate) {
        system.setAddressRate(rate);
        agentSystemRepository.save(system);
    }

    @Override
    public void updateDirectRate(AgentSystem system, int level, BigDecimal rate) {
        if (CollectionUtils.isEmpty(system.getRates())) {
            system.setRates(systemService.defaultAgentRates());
        }
        system.getRates().get(level).setMarketRate(rate);
        agentSystemRepository.save(system);
    }

    @Override
    public void updateIndirectRate(AgentSystem system, int level, BigDecimal rate) {
        if (CollectionUtils.isEmpty(system.getRates())) {
            system.setRates(systemService.defaultAgentRates());
        }
        system.getRates().get(level).setRecommendRate(rate);
        agentSystemRepository.save(system);
    }

    @Override
    public BigDecimal directRate(AgentSystem system, AgentLevel agent) {
        return rate(system, agent, AgentRate::getMarketRate);
    }

    private BigDecimal rate(AgentSystem system, AgentLevel agent, Function<AgentRate, BigDecimal> toRate) {
        Map<Integer, AgentRate> rates = system.getRates();
        if (CollectionUtils.isEmpty(rates)) {
            rates = systemService.defaultAgentRates();
        }

        return toRate.apply(rates.get(agent.getLevel()));
    }

    @Override
    public BigDecimal indirectRate(AgentSystem system, AgentLevel agent) {
        return rate(system, agent, AgentRate::getRecommendRate);
    }

}
