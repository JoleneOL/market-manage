package cn.lmjia.market.core.config.other;

import cn.lmjia.market.core.config.MVCConfig;
import cn.lmjia.market.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * @author CJ
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Import({SecurityConfig.Security.class})
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LoginService loginService;

    @Autowired
    public void registerSharedAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder);
    }

    @EnableWebSecurity
    @Order(99)//毕竟不是老大 100就让给别人了
    public static class Security extends WebSecurityConfigurerAdapter {

        private final MVCConfig mvcConfig;
        @Autowired
        private Environment environment;

        @Autowired
        public Security(MVCConfig mvcConfig) {
            this.mvcConfig = mvcConfig;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);

            web.ignoring()
                    .antMatchers(
                            // 安全系统无关的uri
                            mvcConfig.staticResourceAntPatterns()
                    )
                    // 微信校验
                    .antMatchers("/MP_verify_*.txt", "/**/favicon.ico", "/weixin/sdk/config")
                    // 微信事件
                    .antMatchers("/_weixin_event/")
                    .antMatchers("/404.html", "/500.html")
                    // 畅捷支付回调
                    .antMatchers("/cash/notify/chanpay")
                    // 投融家
                    .antMatchers("/_tourongjia_event_")
                    // paymax 支付回调
                    .antMatchers(environment
                            .getRequiredProperty("com.paymax.spring.hookUriWithoutAppId") + "/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("UTF-8");
            filter.setForceEncoding(true);
            http.addFilterBefore(filter, CsrfFilter.class);

            http.headers().frameOptions().sameOrigin();

            // 在测试环境下 随意上传
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
                    http.antMatcher("/**")
                            .authorizeRequests();

            registry
                    // misc
                    .antMatchers("/misc/sendLoginCode", "/misc/sendRegisterCode").permitAll()
                    // 登录跳转页面
                    .antMatchers("/wechatJoin", "/wechatRegister").permitAll()
                    .antMatchers("/toLoginWechat", "/wechatLogin").permitAll()
                    // 手机号码可用性检测
                    .antMatchers("/loginData/mobileValidation").permitAll()
                    // 首页允许访问
//                    .antMatchers("/").permitAll()
                    // 短链不保护
                    .antMatchers("/t/**").permitAll()
                    // API 不经过安全机制
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/alimama/**").permitAll()
                    .antMatchers("/huotao/**").permitAll()
                    .antMatchers("/weChatGroup/**").permitAll()
                    .antMatchers("/task/**").permitAll()
                    .antMatchers("/hotUser/**").permitAll()
                    .antMatchers("/goods/**").permitAll()
                    //非导购页无需登录访问
                    .antMatchers("/public-platform/**").permitAll()
                    //浏览采集网页面不需要登录
                    .antMatchers("/portal/**").permitAll()
                    .antMatchers("/goodInfoLink/**").permitAll()
                    .antMatchers("/manage/agency/pidList").permitAll()
                    // 其他必须接受保护
                    .antMatchers("/**").authenticated()
//                    .antMatchers("/manage/**").hasAnyRole(Login.Role_Manage_Value, "ROOT")
                    .antMatchers("/manage/root/**").hasRole("ROOT")
// 更多权限控制
                    .and().csrf().disable()
                    .formLogin()
//                .failureHandler()
                    .loginProcessingUrl("/passwordAuth")
                    .loginPage("/toLogin")
                    .failureUrl("/toLogin?type=error")
                    .permitAll()
                    .and()
                    .logout().logoutUrl("/logout").permitAll()
//                    .logoutSuccessUrl("/justLogout").permitAll()
            ;
        }
    }
}
