package cn.lmjia.market.core;

import cn.lmjia.market.core.config.MVCConfig;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = MVCConfig.class)
public abstract class CoreWebTest extends CoreServiceTest {
}
