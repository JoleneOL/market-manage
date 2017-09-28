package me.jiangcai.logistics;

import me.jiangcai.lib.jdbc.JdbcSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author CJ
 */
@Configuration
@Import(JdbcSpringConfig.class)
@ComponentScan({
        "me.jiangcai.logistics.service"
        , "me.jiangcai.logistics.controller"
})
@EnableJpaRepositories("me.jiangcai.logistics.repository")
@EnableWebMvc
public class LogisticsConfig {
    /**
     * 可以发货的角色
     */
    public static final String ROLE_SHIP = "LOGISTICS_SHIP";
}
