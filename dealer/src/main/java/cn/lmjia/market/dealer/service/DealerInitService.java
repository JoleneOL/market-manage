package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * @author CJ
 */
@Service
public class DealerInitService {

    @Autowired
    private Environment environment;
    @Autowired
    private AgentService agentService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @PostConstruct
    @Transactional
    public void defaultAgents() {
        long count = agentLevelRepository.count((root, query, cb) -> cb.isNull(root.get("superior")));
        if (count == 0) {
            Login login = new Login();
            login.setLoginName(environment.getProperty("default.agent.loginName", "master"));
            login = loginService.password(login
                    , environment.getProperty("default.agent.password", "masterIsMaster"));

            agentService.addTopAgent(login, environment.getProperty("default.agent.name", "默认名称"));
        }
    }

}
