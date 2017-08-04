package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.LogisticsConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 需要配置gateway URL，会读取以下系统参数作为URL
 * <ul>
 * <li>haier.gateway.URL 默认http://58.56.128.84:9001/EAI/service/VOM/CommonGetWayToVOM/CommonGetWayToVOM</li>
 * <li>haier.keyValue 约定的keyValue</li>
 * <li>haier.key AES密钥</li>
 * </ul>
 * 而且它将监视/_haier_vom_callback
 *
 * @author CJ
 */
@Configuration
@Import(LogisticsConfig.class)
@EnableWebMvc
@ComponentScan({
        "me.jiangcai.logistics.haier.service", "me.jiangcai.logistics.haier.controller"
})
public class HaierConfig {
}
