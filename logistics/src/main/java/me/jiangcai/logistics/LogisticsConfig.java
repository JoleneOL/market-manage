package me.jiangcai.logistics;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.logistics.service")
@EnableJpaRepositories("me.jiangcai.logistics.repository")
public class LogisticsConfig {
}
