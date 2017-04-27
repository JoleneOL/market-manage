package cn.lmjia.market.web.controller;

import cn.lnjia.market.core.CoreServiceTest;
import cn.lmjia.market.web.config.WebConfig;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = WebConfig.class)
public abstract class WebTest extends CoreServiceTest {
}
