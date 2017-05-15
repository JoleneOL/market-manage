package cn.lnjia.market.core;

import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.EnumUtils;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.lang.RandomStringUtils;
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
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
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
    @Autowired
    protected ResourceService resourceService;
    @Autowired
    private LocalDateConverter localDateConverter;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    /**
     * 新增并且保存一个随机的管理员
     *
     * @return 已保存的管理员
     */
    protected Manager newRandomManager() {
        return newRandomManager(ManageLevel.values());
    }

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param levels 等级;可以为null
     * @return 已保存的管理员
     */
    protected Manager newRandomManager(ManageLevel... levels) {
        return newRandomManager(randomMobile(), UUID.randomUUID().toString(), levels);
    }

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param rawPassword 明文密码
     * @param levels      等级;可以为null
     * @return 已保存的管理员
     */
    protected Manager newRandomManager(String rawPassword, ManageLevel... levels) {
        return newRandomManager(randomMobile(), rawPassword, levels);
    }

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param rawPassword 明文密码
     * @param loginName   指定登录名
     * @param levels      等级;可以为null
     * @return 已保存的管理员
     */
    protected Manager newRandomManager(String loginName, String rawPassword, ManageLevel... levels) {
        Manager manager = new Manager();
        manager.setLoginName(loginName);
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

    /**
     * @return 新生成的图片路径
     */
    protected String newRandomImagePath() throws IOException {
        String path = "tmp/" + UUID.randomUUID().toString() + ".png";
        try (InputStream stream = randomPngImageResource().getInputStream()) {
            resourceService.uploadResource(path, stream);
        }
        return path;
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

    public String toText(LocalDate localDate) {
        return localDateConverter.print(localDate, null);
    }

    /**
     * @param manager 管理员可以么？
     * @return 随便一个已存在的身份
     */
    protected Login randomLogin(boolean manager) {
        return loginRepository.findAll((root, query, cb)
                -> cb.isTrue(root.get("enabled"))).stream()
                .filter(login -> manager || !(login instanceof Manager))
                .max(new RandomComparator())
                .orElseThrow(() -> new IllegalStateException("一个都没有？"));
    }

    /**
     * @return 随机的一个地址
     */
    protected Address randomAddress() {
        Address address = new Address();
        address.setProvince("北京市");
        address.setPrefecture("北京市");
        address.setCounty("东城区");
        address.setOtherAddress("其他地址" + RandomStringUtils.randomAlphabetic(10));
        return address;
    }

    /**
     * @param who       发起者
     * @param recommend 推荐者
     * @return 新增的随机订单
     */
    protected MainOrder newRandomOrderFor(Login who, Login recommend) {
        return mainOrderService.newOrder(who, recommend, "客户" + RandomStringUtils.randomAlphabetic(6)
                , randomMobile(), 20 + random.nextInt(50), EnumUtils.randomEnum(Gender.class)
                , randomAddress()
                , mainGoodRepository.findAll().stream().max(new RandomComparator()).orElse(null)
                , 1 + random.nextInt(10)
                , random.nextBoolean() ? null : UUID.randomUUID().toString().replaceAll("-", ""));
    }
}
