package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.WechatRegisterPage;
import cn.lmjia.market.wechat.page.WechatSharePage;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinTestConfig;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
public class WechatShareControllerTest extends WechatTestBase {

    @Autowired
    private WeixinTestConfig weixinTestConfig;
    private Login currentLogin;

    @Override
    protected Login allRunWith() {
        return currentLogin;
    }

    @Test
    public void share() throws Exception {
        // 正常用户进入
        Login origin = randomLogin(false, false);
        currentLogin = origin;
        // 打开分享页面
        WebDriver originDriver = driver;
        originDriver.get("http://localhost" + SystemService.wechatShareUri);
        WechatSharePage sharePage = initPage(WechatSharePage.class);
        // 模拟新用户分享
        currentLogin = null;
        WeixinUserDetail weixinUser = WeixinUserMocker.randomWeixinUserDetail();
        weixinTestConfig.setNextDetail(weixinUser);
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
            assertThat(loginService.byLoginName(mobile).getGuideUser())
                    .isEqualTo(origin);
        } finally {
            weixinTestConfig.setNextDetail(null);
        }
    }

}