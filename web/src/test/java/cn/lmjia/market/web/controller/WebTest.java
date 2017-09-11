package cn.lmjia.market.web.controller;

import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.web.config.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {WebConfig.class, WebTest.Config.class})
public abstract class WebTest extends DealerServiceTest {

    @Configuration
    @PropertySource("classpath:/local_aliyun.properties")
    public static class Config {

    }
}
