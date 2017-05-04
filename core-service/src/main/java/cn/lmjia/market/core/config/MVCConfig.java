package cn.lmjia.market.core.config;

import cn.lmjia.market.core.enhance.NewSpringResourceTemplateResolver;
import cn.lmjia.market.core.selection.RowDefinitionHandler;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import me.jiangcai.wx.web.thymeleaf.WeixinDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 属于每一个web模块或者web应用程序都应当加载的模块
 *
 * @author CJ
 */
@Configuration
@EnableWebMvc
@Import({MVCConfig.MVCConfigLoader.class})
public class MVCConfig extends WebMvcConfigurerAdapter {
    private static final String UTF8 = "UTF-8";
    private static String[] STATIC_RESOURCE_PATHS = new String[]{
            "assets", "_resources"
    };
    private final ThymeleafViewResolver htmlViewResolver;
    //    private final TokenHotUserResolver tokenHotUserResolver;
//    private final PageAndSelectionResolver pageAndSelectionResolver;
//    private final LocalDateTimeFormatter localDateTimeFormatter;
//    private final DateConverter dateConverter;
    private final Environment environment;
    private final Set<WebModule> webModules;
    @Autowired
    private RowDefinitionHandler rowDefinitionHandler;
//    private final BigDecimalConverter bigDecimalConverter;

    @Autowired
    public MVCConfig(ThymeleafViewResolver htmlViewResolver, Environment environment, Set<WebModule> webModules) {
        this.htmlViewResolver = htmlViewResolver;
        this.environment = environment;
        this.webModules = webModules;
    }

    /**
     * 文件上传
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
//        returnValueHandlers.add(new DrawablePageAndSelectionResolver());
        returnValueHandlers.add(rowDefinitionHandler);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
//        exceptionResolvers.add(new WebHandlerExceptionResolver());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.extendMessageConverters(converters);
        // 必须确保 json MappingJackson2HttpMessageConverter 比 xml MappingJackson2XmlHttpMessageConverter 优先级高
        HttpMessageConverter xml = converters.stream().filter(httpMessageConverter
                -> httpMessageConverter instanceof MappingJackson2XmlHttpMessageConverter)
                .findAny().orElse(null);

        HttpMessageConverter json = converters.stream().filter(httpMessageConverter
                -> httpMessageConverter instanceof MappingJackson2HttpMessageConverter)
                .findAny().orElse(null);

        if (xml != null && json != null) {
            int index = converters.indexOf(xml);
            converters.remove(json);
            converters.add(index, json);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
//        PageableHandlerMethodArgumentResolver resolver =
//                new PageableHandlerMethodArgumentResolver(new SortHandlerMethodArgumentResolver()) {
//                    @Override
//                    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
//                        Pageable result = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
//                        if (result == null)
//                            return null;
//                        return new PageRequest(result.getPageNumber() - 1, result.getPageSize(), result.getSort());
//                    }
//                };
//        resolver.setFallbackPageable(new PageRequest(1, 10));
//        resolver.setPageParameterName(PAGE_PARAMETER_NAME);
//        resolver.setSizeParameterName(SIZE_PARAMETER_NAME);

//        DrawablePageableArgumentResolver resolver = new DrawablePageableArgumentResolver();
//        argumentResolvers.add(resolver);
//        argumentResolvers.add(tokenHotUserResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
//        registry.addFormatterForFieldType(LocalDateTime.class, localDateTimeFormatter);
//        registry.addFormatterForFieldType(BigDecimal.class, bigDecimalConverter);
//        registry.addFormatterForFieldType(Date.class, dateConverter);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        for (String path : STATIC_RESOURCE_PATHS) {
            registry.addResourceHandler("/" + path + "/**").addResourceLocations("/" + path + "/");
        }

        registry.addResourceHandler("/MP_verify_*.txt").addResourceLocations("/");

        webModules.stream()
                .filter(webModule -> webModule.resourcePathPatterns() != null)
                .forEach(webModule -> {
                    Stream.of(webModule.resourcePathPatterns()).forEach(
                            pattern -> {
                                webModule.resourceHandler(pattern, registry.addResourceHandler(pattern));
                            }
                    );
                });

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addRedirectViewController("/**/favicon.ico", "/assets/images/_favicon.ico");
    }

    private ViewResolver redirectViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setViewNames(new String[]{"redirect:*"});
        return resolver;
    }

    private ViewResolver forwardViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setViewNames(new String[]{"forward:*"});
        return resolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(htmlViewResolver);
        registry.viewResolver(redirectViewResolver());
        registry.viewResolver(forwardViewResolver());
    }

    public String[] staticResourceAntPatterns() {
        String[] ignoring;

        Collection<String> otherPaths = webModules.stream()
                .filter(webModule -> webModule.resourcePathPatterns() != null)
                .flatMap(webModule -> Stream.of(webModule.resourcePathPatterns()))
                .collect(Collectors.toSet());

        int startIndex = 0;
        if (environment.acceptsProfiles("development")) {
            ignoring = new String[STATIC_RESOURCE_PATHS.length + 2 + otherPaths.size()];
            ignoring[startIndex++] = "/**/*.html";
            ignoring[startIndex++] = "/mock/**/*";
        } else {
            ignoring = new String[STATIC_RESOURCE_PATHS.length + otherPaths.size()];
        }
        for (String path : STATIC_RESOURCE_PATHS) {
            ignoring[startIndex++] = "/" + path + "/**/*";
        }
        for (String path : otherPaths) {
            ignoring[startIndex++] = path;
        }
        return ignoring;
    }

    @Import(MVCConfigLoader.EngineLoader.class)
    static class MVCConfigLoader {

        private final SpringTemplateEngine htmlViewTemplateEngine;

        @Autowired
        public MVCConfigLoader(SpringTemplateEngine htmlViewTemplateEngine) {
            this.htmlViewTemplateEngine = htmlViewTemplateEngine;
        }

        @Autowired
        public void setTemplateEngineSet(Set<SpringTemplateEngine> templateEngineSet) {
            // 所有都增加安全方言
            templateEngineSet.forEach(engine -> engine.addDialect(new SpringSecurityDialect()));
        }

        @Bean
        public ThymeleafViewResolver htmlViewResolver() {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(htmlViewTemplateEngine);
            resolver.setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
            resolver.setCharacterEncoding(UTF8);
            resolver.setCache(false);
            resolver.setViewNames(new String[]{"*.html"});
            return resolver;
        }

        @ComponentScan("cn.lmjia.market.core.selection")
        @Import(WeixinWebSpringConfig.class)
        @Configuration
//        @ComponentScan("me.jiangcai.wx.web.thymeleaf")
//        @ComponentScan("me.jiangcai.wx.couple")
        static class EngineLoader {
            private final ApplicationContext applicationContext;
            private final Environment environment;
            private final WeixinDialect weixinDialect;
            private final Set<WebModule> webModules;

            @Autowired
            public EngineLoader(ApplicationContext applicationContext, Environment environment
                    , WeixinDialect weixinDialect, Set<WebModule> webModules) {
                this.applicationContext = applicationContext;
                this.environment = environment;
                this.weixinDialect = weixinDialect;
                this.webModules = webModules;
            }

            SpringTemplateEngine templateEngine(Set<ITemplateResolver> templateResolvers) {
                SpringTemplateEngine engine = new SpringTemplateEngine();
                engine.setTemplateResolvers(templateResolvers);
                engine.addDialect(new Java8TimeDialect());
                engine.addDialect(new SpringSecurityDialect());
                engine.addDialect(weixinDialect);
                return engine;
            }

            private SpringResourceTemplateResolver createHtmlTemplateResolver() {
                SpringResourceTemplateResolver resolver = new NewSpringResourceTemplateResolver();
                resolver.setCacheable(!environment.acceptsProfiles("development")
                        && !environment.acceptsProfiles("test"));
                resolver.setCharacterEncoding(UTF8);
                resolver.setApplicationContext(applicationContext);
                resolver.setTemplateMode(TemplateMode.HTML);
                resolver.setOrder(1);
                return resolver;
            }

            @Bean
            public SpringTemplateEngine htmlViewTemplateEngine() {
                final Set<ITemplateResolver> collect = webModules.stream()
                        .filter(WebModule::hasOwnTemplateResolver)
                        .map(webModule -> {
                            SpringResourceTemplateResolver resolver = createHtmlTemplateResolver();
                            webModule.templateResolver(resolver);
                            resolver.setOrder(2);
                            return (ITemplateResolver) resolver;
                        })
                        .collect(Collectors.toSet());

                collect.add(createHtmlTemplateResolver());
                return templateEngine(collect);
            }

        }
    }

}
