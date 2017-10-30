package cn.lmjia.cash.transfer.cjb;

import cn.lmjia.cash.transfer.CashTransferConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author lxf
 *
 */
@Configuration
@Import(CashTransferConfig.class)
@EnableWebMvc
@ComponentScan({
        "cn.lmjia.cash.transfer.cjb.service"
})
public class CjbConfig {
}
