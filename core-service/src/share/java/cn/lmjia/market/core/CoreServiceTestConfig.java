package cn.lmjia.market.core;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.config.MVCConfig;
import cn.lmjia.market.core.config.WebModule;
import cn.lmjia.market.core.util.TestDataSource;
import com.huotu.vefification.test.VerificationCodeTestConfig;
import lombok.SneakyThrows;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.lib.test.config.H2DataSourceConfig;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.test.PaymentTestConfig;
import me.jiangcai.payment.test.service.MockPayToggle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.util.StringUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author CJ
 */
@Configuration
@ImportResource("classpath:/datasource_local.xml")
@PropertySource({"classpath:/test_wx.properties", "classpath:/test_paymax.properties"})
@Import({CoreConfig.class, ChanpayTestSpringConfig.class, PaymentTestConfig.class, VerificationCodeTestConfig.class})
@ComponentScan("cn.lmjia.market.core.test")
public class CoreServiceTestConfig extends H2DataSourceConfig implements WebMvcConfigurer, WebModule {

    private static final Log log = LogFactory.getLog(CoreServiceTestConfig.class);
    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public HaierSupplier haierSupplier() {
        return new HaierSupplier() {
            @Override
            public void cancelOrder(String id, boolean focus, String reason) {

            }

            @Override
            public void updateProduct(Product product) {

            }

            @Override
            public String sign(String content, String keyValue) {
                return null;
            }

            @Override
            public Object event(String businessType, String source, String contentType, String sign, String content) throws IOException {
                return null;
            }

            @Override
            public StockShiftUnit makeShift(LogisticsSource source, LogisticsDestination destination, Consumer<StockShiftUnit> forUnit, int options) {
                HaierOrder unit = new HaierOrder();
                forUnit.accept(unit);
                unit.setOrderNumber(UUID.randomUUID().toString().replaceAll("-", ""));
                //
                return unit;
            }
        };
    }

    @Bean
    @Primary
    public ChanpayPaymentForm chanpayPaymentForm() {
        return new ChanpayPaymentForm() {
            @Override
            public void tradeUpdate(TradeEvent event) throws IOException, SignatureException {

            }

            @Override
            public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
                log.debug("准备创建金额为" + order.getOrderDueAmount() + "的" + order.getOrderProductName() + "订单");
                if (order.getOrderDueAmount().intValue() == 0)
                    throw new IllegalStateException("错误的金额：" + order.getOrderDueAmount());
                ChanpayPayOrder chanpayPayOrder = new ChanpayPayOrder();
                chanpayPayOrder.setPlatformId(UUID.randomUUID().toString());
                if (additionalParameters != null && additionalParameters.containsKey("desktop")) {
                    // successUri
                    // 给一个uri 直接302 successUri
                    chanpayPayOrder.setUrl("/redirectSuccessUri");
                } else
                    chanpayPayOrder.setUrl(UUID.randomUUID().toString());
                return chanpayPayOrder;
            }

            @Override
            public void orderMaintain() {

            }
        };
    }

    @Bean
    public MockPayToggle mockPayToggle() {
        return (payableOrder, payOrder) -> {
            if (MockSetting.AutoPay)
                return 1;
            return null;
        };
    }

    @Bean
    public DataSource dataSource() {
        log.debug("active profiles:" + StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles()));
        if (environment.acceptsProfiles("mysql")) {
            DriverManagerDataSource dataSource;
            if (environment.acceptsProfiles("jdbcProfile"))
                dataSource = new TestDataSource();
            else
                dataSource = new DriverManagerDataSource();

            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            // ?profileSQL=true
            dataSource.setUrl("jdbc:mysql://localhost/market");
            dataSource.setUsername("root");
            return dataSource;
        }
        if (environment.acceptsProfiles("h2file")) {
            return fileDataSource("market");

        }
        return memDataSource("cn/lmjia/market");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 即使没有安全系统；依然可以根据 AuthenticationPrincipal 获取当前登录状态
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Override
    public boolean hasOwnTemplateResolver() {
        return true;
    }

    @Override
    public void templateResolver(SpringResourceTemplateResolver resolver) {
        resolver.setPrefix(urlToWebApp());
    }

    @Override
    public String[] resourcePathPatterns() {
        String[] d = new String[MVCConfig.STATIC_RESOURCE_PATHS.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = "/" + MVCConfig.STATIC_RESOURCE_PATHS[i] + "/**";
        }
        return d;
    }

    @Override
    public void resourceHandler(String patternInput, ResourceHandlerRegistration registration) {
        String pattern = patternInput.substring(1, patternInput.length() - 3);
        registration.addResourceLocations(urlToWebApp() + pattern + "/");
    }

    private String urlToWebApp() {
        if (new File("./web").exists())
            return urlToWebApp(new File("./web"));
        return urlToWebApp(new File("../web"));
    }

    @SneakyThrows
    private String urlToWebApp(File file) {
        return new File(file, "src/main/webapp/").toURI().toURL().toString();
    }
}
