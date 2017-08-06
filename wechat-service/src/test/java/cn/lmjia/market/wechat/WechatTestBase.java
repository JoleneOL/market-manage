package cn.lmjia.market.wechat;


import cn.lmjia.market.core.config.MVCConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.wechat.config.WechatConfig;
import cn.lmjia.market.wechat.page.WechatMyPage;
import cn.lmjia.market.wechat.page.WechatMyTeamPage;
import cn.lmjia.market.wechat.page.WechatOrderListPage;
import cn.lmjia.market.wechat.page.WechatRegisterPage;
import cn.lmjia.market.wechat.page.WechatSharePage;
import com.gargoylesoftware.htmlunit.WebClient;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository;
import me.jiangcai.wx.test.WeixinTestConfig;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.WebConnectionHtmlUnitDriver;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author CJ
 */
@ContextConfiguration(classes = {WeixinTestConfig.class, WechatConfig.class, MVCConfig.class})
public abstract class WechatTestBase extends DealerServiceTest {

    @Autowired
    private WeixinTestConfig weixinTestConfig;
    @Autowired
    private StandardWeixinUserRepository standardWeixinUserRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PublicAccount publicAccount;

    /**
     * @return 生成一个新的微信帐号，并且应用在系统中
     */
    protected WeixinUserDetail nextCurrentWechatAccount() {
        WeixinUserDetail detail = WeixinUserMocker.randomWeixinUserDetail();
        weixinTestConfig.setNextDetail(detail);
        return detail;
    }

    @Override
    protected DefaultMockMvcBuilder buildMockMVC(DefaultMockMvcBuilder builder) {
        return super.buildMockMVC(builder);
    }

    protected MockHttpServletRequestBuilder wechatPost(String urlTemplate, Object... urlVariables) {
        return makeWechat(super.post(urlTemplate, urlVariables));
    }

    protected MockHttpServletRequestBuilder wechatGet(String urlTemplate, Object... urlVariables) {
        return makeWechat(super.get(urlTemplate, urlVariables));
    }

    protected MockMultipartHttpServletRequestBuilder wechatFileUpload(String urlTemplate, Object... urlVariables) {
        return makeWechat(super.fileUpload(urlTemplate, urlVariables));
    }

    @SuppressWarnings("unchecked")
    private <T extends MockHttpServletRequestBuilder> T makeWechat(T builder) {
        return (T) builder.header("user-agent", "MicroMessenger");
    }

    @Override
    protected MockMvcHtmlUnitDriverBuilder buildWebDriver(MockMvcHtmlUnitDriverBuilder mockMvcHtmlUnitDriverBuilder) {
        return mockMvcHtmlUnitDriverBuilder.withDelegate(new WebConnectionHtmlUnitDriver() {
            @Override
            protected WebClient modifyWebClientInternal(WebClient webClient) {
                webClient.addRequestHeader("user-agent", "MicroMessenger");
                return super.modifyWebClientInternal(webClient);
            }
        });
    }

    protected WechatMyPage getWechatMyPage() {
        driver.get("http://localhost" + SystemService.wechatMyURi);
        return initPage(WechatMyPage.class);
    }

    protected WechatMyTeamPage getWechatMyTeamPage() {
        driver.get("http://localhost" + SystemService.wechatMyTeamURi);
        return initPage(WechatMyTeamPage.class);
    }

    protected WechatOrderListPage getWechatOrderListPage() {
        driver.get("http://localhost/wechatOrderList");
        return initPage(WechatOrderListPage.class);
    }

    /**
     * 绑定开发者微信号到该登录
     *
     * @param login 登录
     */
    protected void bindDeveloperWechat(Login login) {
        StandardWeixinUser weixinUser = standardWeixinUserRepository.findByOpenId("oiKvNt0neOAB8ddS0OzM_7QXQDZw");
        if (weixinUser == null) {
            weixinUser = new StandardWeixinUser();
            weixinUser.setOpenId("oiKvNt0neOAB8ddS0OzM_7QXQDZw");
            weixinUser.setAppId(publicAccount.getAppID());
            weixinUser = standardWeixinUserRepository.save(weixinUser);
        }
        login = loginRepository.getOne(login.getId());
        login.setWechatUser(weixinUser);
        loginRepository.save(login);
    }

    protected void makeSuccessOrder(Login to) {
        MainOrder order = newRandomOrderFor(to, to);
        makeOrderPay(order);
        makeOrderDone(order);
    }

    protected Login createNewUserByShare() {
        Login origin = randomLogin(false, false);
        updateAllRunWith(origin);
        // 需要确保这个用户是一个正式用户，否者会无法打开分享页面
        makeSuccessOrder(origin);
        // 打开分享页面
        WebDriver originDriver = driver;
        originDriver.get("http://localhost" + SystemService.wechatShareUri);
        WechatSharePage sharePage = initPage(WechatSharePage.class);
        // 模拟新用户分享
        updateAllRunWith(null);
        WeixinUserDetail weixinUser = WeixinUserMocker.randomWeixinUserDetail();
        weixinTestConfig.setNextDetail(weixinUser);
        final Login newUser;
        try {
            createWebDriver();// 这个时候的driver就是新用户专用的了

            // 模拟新用户进入
            driver.get(sharePage.getShareUrl());
            // 应该进入注册
            WechatRegisterPage registerPage = initPage(WechatRegisterPage.class);

            // 完成注册
            String mobile = randomMobile();
            registerPage.sendAuthCode(mobile);
            registerPage.submitSuccessAs(RandomStringUtils.randomAlphabetic(10));
            // 应该进入下单
            assertThat(driver.getTitle())
                    .isEqualTo("我的下单");

            // 这个用户肯定是来自 origin 的
            newUser = loginService.byLoginName(mobile);
            assertThat(newUser.getGuideUser())
                    .isEqualTo(origin);
        } finally {
            weixinTestConfig.setNextDetail(null);
            originDriver.close();
        }
        return newUser;
    }


    /**
     * 订单申请
     *
     * @param request 订单申请内容
     * @return 完成之后的跳转URL
     * @throws Exception
     */
    protected String submitOrderRequest(OrderRequest request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                orderRequestBuilder(wechatPost("/wechatOrder"), request);

        return mockMvc.perform(
                requestBuilder
        )
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getHeader("Location");
    }
}
