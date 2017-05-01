package cn.lmjia.market.core.enhance;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.templateresource.SpringResourceTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;

/**
 * @author CJ
 */
public class NewSpringResourceTemplateResolver extends SpringResourceTemplateResolver {

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate
            , String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        SpringResourceTemplateResource resourceTemplateResource = (SpringResourceTemplateResource)
                super.computeTemplateResource(configuration, ownerTemplate, template, resourceName, characterEncoding
                        , templateResolutionAttributes);
        if (resourceTemplateResource.exists())
            return resourceTemplateResource;
        return null;
    }
}
