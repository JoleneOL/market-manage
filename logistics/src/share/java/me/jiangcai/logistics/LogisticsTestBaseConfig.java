package me.jiangcai.logistics;

import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author CJ
 */ // 还有基本数据源
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
@Import(LogisticsConfig.class)
@ImportResource("classpath:/datasource_local.xml")
@ComponentScan("me.jiangcai.logistics.demo")
@Configuration
public class LogisticsTestBaseConfig extends H2DataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        if (environment.acceptsProfiles("mysql")) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost/logistics");
            dataSource.setUsername("root");
            return dataSource;
        }
        if (environment.acceptsProfiles("h2file")) {
            return fileDataSource("logistics");
        }
        return memDataSource("logistics");
    }
}
