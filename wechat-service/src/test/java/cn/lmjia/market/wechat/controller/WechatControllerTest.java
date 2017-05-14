package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.LoginPage;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;

/**
 * @author CJ
 */
public class WechatControllerTest extends WechatTestBase {

    @Test
    public void newWechat() throws Exception {
        WeixinUserDetail detail = nextCurrentWechatAccount();

        // 使用一个陌生的微信用户 打开 /toLoginWechat 会跳转到 登录界面
        // 完成之后 则立刻跳转到主页
        // 下次再使用该帐号登录则直接来到主页

        driver.get("http://localhost/toLoginWechat");
        LoginPage loginPage = initPage(LoginPage.class);
        loginPage.printThisPage();
    }

    @Test
    public void existingWechat() {
        // 已登录的微信号 直接来到主页
    }

}