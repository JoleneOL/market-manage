package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private Environment environment;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private SystemStringService systemStringService;

    @Override
    public String toUrl(String uri) {
        return environment.getProperty("market.url", "http://localhost") + uri;
    }

    @Override
    public boolean allowWithdrawDisplay(Login login) {
        return login != null && agentLevelRepository.findByLogin(login).size() != 0;
    }

    @Override
    public void updateNonAgentAbleToGainCommission(boolean value) {
        systemStringService.updateSystemString("market.NonAgentAbleToGainCommission", value);
    }

    @Override
    public boolean isNonAgentAbleToGainCommission() {
        return systemStringService.getCustomSystemString("market.NonAgentAbleToGainCommission"
                , "market.NonAgentAbleToGainCommission.comment", true, Boolean.class, true);
    }

    @Override
    public void updateRegularLoginAsAnyOrder(boolean value) {
        systemStringService.updateSystemString("market.RegularLoginAsAnyOrder", value);
    }

    @Override
    public boolean isRegularLoginAsAnyOrder() {
        return systemStringService.getCustomSystemString("market.RegularLoginAsAnyOrder"
                , "market.RegularLoginAsAnyOrder.comment", true, Boolean.class, true);
    }
}
