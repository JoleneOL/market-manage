package cn.lnjia.market.core;

import cn.lmjia.market.core.config.CoreConfig;
import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * @author CJ
 */
@Configuration
@ImportResource("classpath:/datasource_local.xml")
@PropertySource("classpath:/test_wx.properties")
@Import(CoreConfig.class)
public class CoreServiceTestConfig extends H2DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        return memDataSource("cn/lmjia/market");
    }
}
