package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.page.WechatOrderPageForHB;

/**
 * 针对花呗的订单测试
 *
 * @author CJ
 */
public class WechatMainOrderControllerTest3 extends AbstractWechatMainOrderControllerTest<WechatOrderPageForHB> {

    @Override
    protected WechatOrderPageForHB openOrderPage() {
        driver.get("http://localhost" + SystemService.wechatOrderURiHB);
        return initPage(WechatOrderPageForHB.class);
    }
}
