package me.jiangcai.logistics;

import me.jiangcai.lib.jdbc.JdbcSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author CJ
 */
@Configuration
@Import(JdbcSpringConfig.class)
@ComponentScan("me.jiangcai.logistics.service")
@EnableJpaRepositories("me.jiangcai.logistics.repository")
public class LogisticsConfig {
}
