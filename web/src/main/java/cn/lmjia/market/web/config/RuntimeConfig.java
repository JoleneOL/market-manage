package cn.lmjia.market.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 运行时配置
 *
 * @author slt
 */
@Configuration
@ImportResource("classpath:/datasource_runtime.xml")
class RuntimeConfig {

}
