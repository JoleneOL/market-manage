package cn.lnjia.market.core;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.seext.EnumUtils;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = CoreServiceTestConfig.class)
@WebAppConfiguration
public abstract class CoreServiceTest extends SpringWebTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(CoreServiceTest.class);
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

    protected BufferedImage randomImage() throws IOException {
        try (InputStream inputStream = randomPngImageResource().getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }

    protected Resource randomPngImageResource() {
        return new ClassPathResource("/images/logo.png");
    }

    protected ResultMatcher similarSelect2(String resource) {
        return result -> {
            Resource resource1 = context.getResource(resource);
            try (InputStream inputStream = resource1.getInputStream()) {
                JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                assertThat(actual.get("total_count").isNumber())
                        .isTrue();
//                assertThat(actual.get("incomplete_results").isBoolean())
//                        .isTrue();

                JsonNode rows = actual.get("items");
                assertThat(rows.isArray())
                        .isTrue();
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验");
                    return;
                }
                JsonNode exceptedAll = objectMapper.readTree(inputStream);
                JsonNode excepted = exceptedAll.get("items").get(0);

                assertSimilarJsonObject(rows.get(0), excepted);
            }
        };
    }
}
