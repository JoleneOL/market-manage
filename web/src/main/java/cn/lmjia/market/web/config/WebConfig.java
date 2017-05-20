package cn.lmjia.market.web.config;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.config.MVCConfig;
import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.dealer.config.DealerConfig;
import cn.lmjia.market.manage.config.ManageConfig;
import cn.lmjia.market.wechat.config.WechatConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * web 模块的配置
 *
 * @author CJ
 */
@Configuration
@Import({ManageConfig.class, DealerConfig.class, WechatConfig.class, MVCConfig.class, CoreConfig.class, SecurityConfig.class})
@ComponentScan({"cn.lmjia.market.web.controller"})
public class WebConfig {
}
