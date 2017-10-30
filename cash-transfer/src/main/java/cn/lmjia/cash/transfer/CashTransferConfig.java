package cn.lmjia.cash.transfer;

import me.jiangcai.lib.jdbc.JdbcSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author lxf
 */
@Configuration
@Import(JdbcSpringConfig.class)
@ComponentScan({
        "cn.lmjia.cash.transfer.service"
})
@EnableWebMvc
public class CashTransferConfig {
}
