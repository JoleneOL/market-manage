package cn.lmjia.market.manage.config;

import cn.lmjia.market.core.config.WebModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

/**
 * @author CJ
 */
@Configuration
public class ManageConfig implements WebModule {
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
