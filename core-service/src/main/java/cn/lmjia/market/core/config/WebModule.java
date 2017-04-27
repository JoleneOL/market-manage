package cn.lmjia.market.core.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

/**
 * web模块
 *
 * @author CJ
 */
public interface WebModule {

    /**
     * @return 是否拥有一个自己的模板资源寻找机制
     */
    boolean hasOwnTemplateResolver();

    /**
     * 渲染自己的模板资源寻找机制
     *
     * @param resolver 即将被渲染的处理器
     */
    void templateResolver(SpringResourceTemplateResolver resolver);

    /**
     * @return 静态资源请求URI的pattern;可以为null
     */
    String[] resourcePathPatterns();

    /**
     * 注册该资源URI
     *
     * @param pattern      URI的pattern
     * @param registration 可以表示何处获取资源
     * @see #resourcePathPatterns()
     */
    void resourceHandler(String pattern, ResourceHandlerRegistration registration);
}
