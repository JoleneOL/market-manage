package cn.lnjia.market.core;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.lib.seext.EnumUtils;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * @author CJ
 */
@ContextConfiguration(classes = CoreServiceTestConfig.class)
@WebAppConfiguration
public abstract class CoreServiceTest extends SpringWebTest {
    @Autowired
    protected LoginService loginService;

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param rawPassword 明文密码
     * @param levels      等级;可以为null
     * @return 已保存的管理员
     */
    protected Manager newRandomManager(String rawPassword, ManageLevel... levels) {
        Manager manager = new Manager();
        manager.setLoginName(randomMobile());
        manager.setLevel(EnumUtils.randomEnum(ManageLevel.class, levels));
        return loginService.password(manager, rawPassword);
    }

    /**
     * 以login身份运行一段代码
     *
     * @param login    身份
     * @param callable 代码
     */
    protected void runWith(Login login, Callable<?> callable) throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        try {
            SecurityContextImpl securityContext1 = new SecurityContextImpl();
            securityContext1.setAuthentication(new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return login.getAuthorities();
                }

                @Override
                public Object getCredentials() {
                    return login;
                }

                @Override
                public Object getDetails() {
                    return login;
                }

                @Override
                public Object getPrincipal() {
                    return login;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return login.getLoginName();
                }
            });
            SecurityContextHolder.setContext(securityContext1);
            callable.call();
        } finally {
            SecurityContextHolder.setContext(securityContext);
        }
    }
}
