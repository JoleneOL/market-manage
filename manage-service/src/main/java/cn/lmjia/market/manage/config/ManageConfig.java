package cn.lmjia.market.manage.config;

import cn.lmjia.market.core.config.WebModule;
import cn.lmjia.market.dealer.config.DealerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

/**
 * @author CJ
 */
@Configuration
@Import(DealerConfig.class)
@ComponentScan({
        "cn.lmjia.market.manage.controller"
})
public class ManageConfig implements WebModule {
    @Override
    public boolean hasOwnTemplateResolver() {
        return true;
    }

    @Override
    public void templateResolver(SpringResourceTemplateResolver resolver) {
        resolver.setPrefix("classpath:/manage-view/");
    }

    @Override
    public String[] resourcePathPatterns() {
        return new String[]{"/manage-resource/**"};
    }

    @Override
    public void resourceHandler(String pattern, ResourceHandlerRegistration registration) {
        switch (pattern) {
            case "/manage-resource/**":
                registration.addResourceLocations("classpath:/manage-view/manage-resource/");
                break;
        }
    }
}
