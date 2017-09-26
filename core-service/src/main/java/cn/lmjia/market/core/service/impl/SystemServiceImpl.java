package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CJ
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private Environment environment;
    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @Override
    public String toUrl(String uri) {
        return environment.getProperty("market.url", "http://localhost") + uri;
    }

    @Override
    public boolean allowWithdrawDisplay(Login login) {
        if(login == null){
            return false;
        }
        return agentLevelRepository.findByLogin(login).size()!=0;
    }
}
