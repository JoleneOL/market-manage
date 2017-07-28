package cn.lmjia.market.core.config;

import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import me.jiangcai.payment.chanpay.PaymentChanpayConfig;
import me.jiangcai.payment.paymax.PaymentPaymaxConfig;
import me.jiangcai.user.notice.wechat.UserNoticeWechatConfig;
import me.jiangcai.wx.standard.StandardWeixinConfig;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
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
@PropertySource("classpath:/defaultSystem.properties")
@Import({CommonConfig.class, DataSupportConfig.class, StandardWeixinConfig.class, PaymentChanpayConfig.class
        , PaymentPaymaxConfig.class, UserNoticeWechatConfig.class, TRJEnhanceConfig.class

//        ServiceConfig.class
})
@ComponentScan({
        "cn.lmjia.market.core.service"
        , "cn.lmjia.market.core.converter"
})
@EnableJpaRepositories("cn.lmjia.market.core.repository")
public class CoreConfig extends WeixinWebSpringConfig implements WebModule {
    // SchedulingConfigurer
    // @EnableScheduling

    /**
     * 单元测试的时候
     */
    public static final String ProfileUnitTest = "unit_test";

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

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("coreMessage");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        return resourceBundleMessageSource;
    }
}
