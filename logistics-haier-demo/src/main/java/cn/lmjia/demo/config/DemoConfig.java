package cn.lmjia.demo.config;

import me.jiangcai.lib.spring.logging.LoggingConfig;
import me.jiangcai.logistics.haier.HaierConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author CJ
 */
@Import({
        LoggingConfig.class, HaierConfig.class
})
@ComponentScan({"cn.lmjia.demo.controller"})
@EnableWebMvc
@Configuration
public class DemoConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        // 允许访问 index.html
        // 以及 js/
        registry.addResourceHandler("/index.html").addResourceLocations("/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    }

}
