package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.WechatGoodAdvanceOrderPage;
import org.junit.Test;

/**
 * 测试预付货款下单
 *
 * @author CJ
 */
public class WechatGoodAdvanceOrderControllerTest extends WechatTestBase {

    @Test
    public void go() {
        // 一个崭新的代理商 自然会因为余额不足 无法下单
        Login login = newRandomAgent();
        updateAllRunWith(login);

        WechatGoodAdvanceOrderPage page = WechatGoodAdvanceOrderPage.of(this, driver);

        page.submitRandomOrder(null, null);
        page.printThisPage();

    }

}