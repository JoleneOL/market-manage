package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @Override
    public AgentLevel addTopAgent(Login login, String name) {
        AgentLevel current = null;
        AgentLevel topLevel = null;
        int count = systemLevel();
        while (count-- > 0) {
            AgentLevel top = new AgentLevel();
            top.setLogin(login);
            top.setRank(name);
            top.setSuperior(current);
            current = agentLevelRepository.save(top);
            if (topLevel == null) {
                topLevel = current;
            }
        }
        // 先设置最高级别的
        return topLevel;
    }
}
