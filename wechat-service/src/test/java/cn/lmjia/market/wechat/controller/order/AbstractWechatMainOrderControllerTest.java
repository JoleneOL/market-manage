package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import cn.lmjia.market.wechat.page.WechatOrderPage;
import org.junit.Test;

/**
 * @author CJ
 */
public abstract class AbstractWechatMainOrderControllerTest<T extends WechatOrderPage> extends WechatTestBase {

    /**
     * @return GET到下单页面的URI
     */
    protected abstract T openOrderPage();

    void doOrder() throws Exception {
        T page = openOrderPage();
        page.submitRandomOrder(null, null);
        PaySuccessPage.waitingForSuccess(this, driver, 3, "http://localhost/wechatPaySuccess?mainOrderId=1");
        // 然后模拟订单完成支付
    }

    @Test
    public void makeOrder() throws Exception {
        // 在微信端发起请求
        Login login1 = randomLogin(false);
        // 特别的设计，让这个帐号绑定到开发个人微信openId 确保可以收到消息
        bindDeveloperWechat(login1);
        updateAllRunWith(login1);

        doOrder();
        // 客户也可以下单
//        final String customerMobile = randomMobile();
//        newRandomOrderFor(login1, login1, customerMobile);
//
//        updateAllRunWith(loginService.byLoginName(customerMobile));
//        doOrder();
    }

}