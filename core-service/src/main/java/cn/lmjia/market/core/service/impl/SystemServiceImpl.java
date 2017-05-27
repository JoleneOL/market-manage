package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.service.SystemService;
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

    @Override
    public String toUrl(String uri) {
        return environment.getProperty("market.url", "http://localhost") + uri;
    }
}
