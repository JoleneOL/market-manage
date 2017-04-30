package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<AgentLevel> manageable(Login login, Pageable pageable) {
        if (login.isManageable())
            return agentLevelRepository.findAll(pageable);
        return agentLevelRepository.findAll(s(highestAgent(login)), pageable);
    }

    private Specification<AgentLevel> s(AgentLevel agent) {
        return (root, query, cb) -> cb.equal(root.get("superior"), agent);
    }

    @Override
    public AgentLevel highestAgent(Login login) {
        AgentLevel agentLevel = agentLevelRepository.findByLogin(login).stream()
                .findAny().orElse(null);
        if (agentLevel != null) {
            // 最高级别的
            AgentLevel current = agentLevel;
            while (current.getSuperior() != null && current.getSuperior().getLogin().equals(login)) {
                current = current.getSuperior();
            }
            return current;
        }
        return null;
    }
}
