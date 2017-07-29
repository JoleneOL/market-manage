package cn.lmjia.market.core;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.test.QuickPayBean;
import cn.lmjia.market.core.util.LoginAuthentication;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ActiveProfiles({"test", CoreConfig.ProfileUnitTest})
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
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private QuickPayBean quickPayBean;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private QuickTradeService quickTradeService;
    private Login allRunWith;

    //<editor-fold desc="自动登录相关">

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
    //</editor-fold>

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
        manager.setLevelSet(Collections.singleton(EnumUtils.randomEnum(ManageLevel.class, levels)));
        return loginService.password(manager, null, rawPassword);
    }

    /**
     * @param target
     * @see #allRunWith()
     */
    protected void updateAllRunWith(Login target) {
        allRunWith = target;
    }

    /**
     * 可以覆盖该方法设定每次测试都将以该身份进行
     *
     * @return 模拟身份
     * @see #runWith(Login, Callable)
     */
    protected Login allRunWith() {
        return allRunWith;
    }

    @Override
    protected final Authentication autoAuthentication() {
        Login login = allRunWith();
        if (login == null)
            return null;
        return new LoginAuthentication(login.getId(), loginService);
    }

    /**
     * 以login身份运行一段代码
     *
     * @param login    身份
     * @param callable 代码
     */
    protected void runWith(Login login, Callable<?> callable) throws Exception {
        Login oldAll = allRunWith;
        updateAllRunWith(login);
//        SecurityContext securityContext = SecurityContextHolder.getContext();
        try {
//            loginAs(login);
            callable.call();
        } finally {
            updateAllRunWith(oldAll);
//            SecurityContextHolder.setContext(securityContext);
        }
    }

    private void loginAs(final Login login) {
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
        return randomLogin(manager, true);
    }

    /**
     * @param manager  管理员可以么？
     * @param customer 客户可以么？
     * @return 随便一个已存在的身份
     */
    protected Login randomLogin(boolean manager, boolean customer) {
        return loginRepository.findAll((root, query, cb)
                -> cb.isTrue(root.get("enabled"))).stream()
                .filter(login -> manager || !(login instanceof Manager))
                .filter(login -> {
                    if (customer)
                        return true;
                    // 排除掉客户
                    return !customerRepository.findAll().stream()
                            .map(Customer::getLogin)
                            .collect(Collectors.toList())
                            .contains(login);
                })
                .max(new RandomComparator())
                .orElseThrow(() -> new IllegalStateException("一个都没有？"));
    }

    /**
     * @return 随机的一个地址
     */
    protected Address randomAddress() {
        Address address = new Address();
        address.setProvince(RandomStringUtils.randomAlphabetic(4) + "省");
        address.setPrefecture(RandomStringUtils.randomAlphabetic(4) + "市");
        address.setCounty(RandomStringUtils.randomAlphabetic(4) + "区");
        address.setOtherAddress("其他地址" + RandomStringUtils.randomAlphabetic(10));
        return address;
    }

    /**
     * @param who       发起者
     * @param recommend 推荐者
     * @return 新增的随机订单
     */
    protected MainOrder newRandomOrderFor(Login who, Login recommend) {
        return newRandomOrderFor(who, recommend, randomMobile());
    }

    /**
     * @param who       发起者
     * @param recommend 推荐者
     * @param mobile    客户手机号码
     * @return 新增的随机订单
     */
    protected MainOrder newRandomOrderFor(Login who, Login recommend, String mobile) {
        return mainOrderService.newOrder(who, recommend, "客户" + RandomStringUtils.randomAlphabetic(6)
                , mobile, 20 + random.nextInt(50), EnumUtils.randomEnum(Gender.class)
                , randomAddress()
                , mainGoodRepository.findAll().stream().max(new RandomComparator()).orElse(null)
                , 1 + random.nextInt(10)
                , random.nextBoolean() ? null : UUID.randomUUID().toString().replaceAll("-", ""));
    }

    /**
     * @param builder 模拟请求构造器
     * @param request 下单原请求
     * @return 执行下单请求
     */
    protected MockHttpServletRequestBuilder orderRequestBuilder(MockHttpServletRequestBuilder builder, OrderRequest request) {
        final MockHttpServletRequestBuilder newBuilder = builder.contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", request.getName())
                .param("age", String.valueOf(request.getAge()))
                .param("gender", String.valueOf(request.getGender()))
                .param("address", request.getAddress().getStandardWithoutOther())
                .param("fullAddress", request.getAddress().getOtherAddress())
                .param("mobile", request.getMobile())
                .param("goodId", String.valueOf(request.getGood().getId()))
                .param("leasedType", request.getGood().getProduct().getCode())
                .param("amount", String.valueOf(request.getAmount()))
                .param("activityCode", request.getCode())
                .param("recommend", String.valueOf(request.getRecommend().getId()));
        if (StringUtils.isEmpty(request.getAuthorising()))
            return newBuilder;
        return newBuilder.param("authorising", request.getAuthorising())
                .param("idNumber", request.getIdNumber());
    }


    /**
     * @return 随机的下单请求原数据
     */
    protected OrderRequest randomOrderRequest() {
        return randomOrderRequest(null, null);
    }

    /**
     * 使用MVC的方式添加一个按揭码
     *
     * @param authorising
     * @param idNumber
     * @throws Exception
     */
    protected void addAuthorising(String authorising, String idNumber) throws Exception {
        // 无需安全
        Login current = allRunWith;
        try {
            mockMvc.perform(post("/_tourongjia_event_")
                    .param("event", "code")
                    .param("authorising", authorising)
                    .param("idNumber", idNumber)
            )
                    .andExpect(status().isOk())
                    .andExpect(similarJsonObjectAs("classpath:/mock/trj_response.json"));
        } finally {
            allRunWith = current;
        }
    }

    /**
     * @return 随机的下单请求原数据
     */
    protected OrderRequest randomOrderRequest(String authorising, String idNumber) {
        Address address = randomAddress();
        MainGood good = mainGoodRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        String code = random.nextBoolean() ? null : UUID.randomUUID().toString().replaceAll("-", "");
        Login recommend = randomLogin(true);
        final String name = "W客户" + RandomStringUtils.randomAlphabetic(6);
        final int age = 20 + random.nextInt(50);
        final int gender = 1 + random.nextInt(2);
        final String mobile = randomMobile();
        final int amount = 1 + random.nextInt(10);
        return new OrderRequest(
                address, good, code
                , recommend, name, age, gender
                , mobile, amount
                , authorising, idNumber
        );
    }

    /**
     * 让这个订单立刻完成支付！
     *
     * @param order
     */
    protected void makeOrderPay(MainOrder order) {
        quickPayBean.makePay(order);
    }

    /**
     * 让订单立马完成
     *
     * @param order order
     */
    protected void makeOrderDone(MainOrder order) {
        quickTradeService.makeDone(order);
    }
}
