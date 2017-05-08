package cn.lmjia.market.core.config;

import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

/**
 * 唯一入口
 *
 * @author CJ
 */
@Configuration
@Import({CommonConfig.class, DataSupportConfig.class,
//        ServiceConfig.class
})
@ComponentScan({
        "cn.lmjia.market.core.service"
        , "cn.lmjia.market.core.converter"
})
@EnableJpaRepositories("cn.lmjia.market.core.repository")
public class CoreConfig extends WeixinWebSpringConfig implements WebModule {

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Override
    public boolean hasOwnTemplateResolver() {
        return false;
    }

    @Override
    public void templateResolver(SpringResourceTemplateResolver resolver) {

    }

    @Override
    public String[] resourcePathPatterns() {
        return null;
    }

    @Override
    public void resourceHandler(String pattern, ResourceHandlerRegistration registration) {

    }
}
