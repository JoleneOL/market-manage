package cn.lmjia.market.dealer.config;

import cn.lmjia.market.core.config.MVCConfig;
import cn.lmjia.market.core.config.WebModule;
import cn.lmjia.market.dealer.mvc.AgentLevelArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

import java.util.List;

/**
 * 可支持配置
 * <ul>
 * <li><b>default.agent.loginName</b>默认顶级代理商登录名</li>
 * <li><b>default.agent.password</b>默认顶级代理商登录密码</li>
 * <li><b>default.agent.name</b>默认顶级代理商 级别名称</li>
 * </ul>
 *
 * @author CJ
 */
@Configuration
@ComponentScan("cn.lmjia.market.dealer.controller")
@EnableJpaRepositories("cn.lmjia.market.dealer.repository")
@Import({MVCConfig.class, DealerConfig.First.class})
public class DealerConfig extends WebMvcConfigurerAdapter implements WebModule {

    @Autowired
    private AgentLevelArgumentResolver agentLevelArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(agentLevelArgumentResolver);
    }

    @Override
    public boolean hasOwnTemplateResolver() {
        return true;
    }

    @Override
    public void templateResolver(SpringResourceTemplateResolver resolver) {
        resolver.setPrefix("classpath:/dealer-view/");
    }

    @Override
    public String[] resourcePathPatterns() {
        return new String[]{"/dealer-resource/**"};
    }

    @Override
    public void resourceHandler(String pattern, ResourceHandlerRegistration registration) {
        switch (pattern) {
            case "/dealer-resource/**":
                registration.addResourceLocations("classpath:/dealer-view/dealer-resource/");
                break;
        }
    }

    @Configuration
    @ComponentScan({"cn.lmjia.market.dealer.mvc", "cn.lmjia.market.dealer.service"})
    @EnableJpaRepositories("cn.lmjia.market.dealer.repository")
    static class First {
    }
}
