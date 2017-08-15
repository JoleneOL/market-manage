package cn.lmjia.market.core;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.test.QuickPayBean;
import cn.lmjia.market.core.util.LoginAuthentication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.EnumUtils;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
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
    private QuickPayBean quickPayBean;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private QuickTradeService quickTradeService;
    private Login allRunWith;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ReadService readService;

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
     * @return 新增一个普通身份
     */
    protected Login newRandomLogin() {
        return loginService.newLogin(Login.class, randomMobile(), randomLogin(false), randomMobile());
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
                .orElseGet(() -> loginService.newLogin(Login.class, randomMobile(), null, randomMobile()));
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
        MockHttpServletRequestBuilder newBuilder = builder.contentType(MediaType.APPLICATION_FORM_URLENCODED)
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
        if (request.getChannelId() != null)
            newBuilder = newBuilder.param("channelId", String.valueOf(request.getChannelId()));

        if (StringUtils.isEmpty(request.getAuthorising()))
            return newBuilder;
        return newBuilder.param("authorising", request.getAuthorising())
                .param("idNumber", request.getIdNumber());
    }


    /**
     * @return 随机的下单请求原数据
     */
    protected OrderRequest randomOrderRequest() {
        return randomOrderRequest(null, null, null, null);
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
    protected OrderRequest randomOrderRequest(Long channelId, MainGood goodInput, String authorising, String idNumber) {
        Address address = randomAddress();
        MainGood good;
        if (goodInput == null)
            good = mainGoodRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        else
            good = goodInput;
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
                , authorising, idNumber, channelId
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

    /**
     * 让物流系统随便找一个仓库或者新建一个仓库给订单配货
     *
     * @param order         order
     * @param depotSupplier 新仓库构造器 可选
     * @param supplierType  物流供应商 可选
     * @return 物流订单
     */
    protected StockShiftUnit logisticsForMainOrderFromAnyDepot(MainOrder order, Supplier<Depot> depotSupplier
            , Class<? extends LogisticsSupplier> supplierType) {
        // 先找仓库呗
        Depot depot = findOrCreateEnableDepot(depotSupplier);

        // MarketBuildInLogisticsSupplier
        return mainOrderService.makeLogistics(supplierType == null ? MarketBuildInLogisticsSupplier.class : supplierType
                , order.getId(), depot.getId());
    }

    /**
     * @param depotSupplier 如果需要新仓库的话 新仓库的构造器
     * @return 找一个可用或者新建一个可用的仓库 同时也是符合新仓库需要的
     */
    private Depot findOrCreateEnableDepot(Supplier<Depot> depotSupplier) {
        return readService.allEnabledDepot().stream()
                .filter(depot -> depotSupplier == null || depot.getClass().equals(depotSupplier.get().getClass()))
                .max(new RandomComparator())
                .orElseGet(() -> {
                    Depot depot = depotSupplier == null ? new Depot() : depotSupplier.get();
                    depot.setAddress(randomAddress());
                    depot.setChargePeopleMobile(randomMobile());
                    depot.setChargePeopleName(RandomStringUtils.randomAlphabetic(5) + "名字");
                    depot.setEnable(true);
                    depot.setCreateTime(LocalDateTime.now());
                    depot.setName(RandomStringUtils.randomAlphabetic(5) + "仓库名字");
                    return depotRepository.saveAndFlush(depot);
                });
    }
}
