package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.page.WechatOrderPage;

/**
 * @author CJ
 */
public class WechatMainOrderControllerTest extends AbstractWechatMainOrderControllerTest {

    @Override
    protected WechatOrderPage openOrderPage() {
        driver.get("http://localhost" + SystemService.wechatOrderURi);
        return initPage(WechatOrderPage.class);
    }

}