package cn.lmjia.market.wechat.config;

import cn.lmjia.market.core.config.WebModule;
import cn.lmjia.market.dealer.config.DealerConfig;
import me.jiangcai.wx.WeixinSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.templateresource.SpringResourceTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author CJ
 */
@Configuration
@ComponentScan(
        {
                "cn.lmjia.market.wechat.controller"
                , "cn.lmjia.market.wechat.service"
        }
)
@Import({DealerConfig.class, WeixinSpringConfig.class})
@EnableJpaRepositories(basePackages = "cn.lmjia.market.wechat.repository")
public class WechatConfig implements WebModule {
    @Override
    public boolean hasOwnTemplateResolver() {
        return true;
    }

    @Override
    public Supplier<SpringResourceTemplateResolver> templateResolverSupplier() {
        return () -> new SpringResourceTemplateResolver() {
            @Override
            protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration
                    , String ownerTemplate, String template, String resourceName, String characterEncoding
                    , Map<String, Object> templateResolutionAttributes) {
                // 只处理  xx@wechat 的模板
                if (!template.startsWith("wechat@"))
                    return null;
                // 然后把相关的名字去掉
//                String newTemplate = template.substring("wechat@".length());
                String newResourceName = resourceName.replaceFirst("wechat@", "");
                SpringResourceTemplateResource resourceTemplateResource = (SpringResourceTemplateResource)
                        super.computeTemplateResource(configuration, ownerTemplate, template, newResourceName, characterEncoding
                                , templateResolutionAttributes);
                if (resourceTemplateResource.exists())
                    return resourceTemplateResource;
                return null;
            }
        };
    }

    @Override
    public void templateResolver(SpringResourceTemplateResolver resolver) {
        resolver.setPrefix("classpath:/wechat-view/");
    }

    @Override
    public String[] resourcePathPatterns() {
        return new String[]{"/wechat-resource/**"};
    }

    @Override
    public void resourceHandler(String pattern, ResourceHandlerRegistration registration) {
        switch (pattern) {
            case "/wechat-resource/**":
                registration.addResourceLocations("classpath:/wechat-resource/");
                break;
            default:
        }
    }
}
