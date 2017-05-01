package cn.lmjia.market.web.controller;

import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.web.config.WebConfig;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = WebConfig.class)
public abstract class WebTest extends DealerServiceTest {
}
