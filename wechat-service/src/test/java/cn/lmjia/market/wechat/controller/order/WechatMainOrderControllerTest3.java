package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.wechat.page.WechatOrderPage;

/**
 * 针对花呗的订单测试
 *
 * @author CJ
 */
public class WechatMainOrderControllerTest3 extends AbstractWechatMainOrderControllerTest {

    @Override
    protected OrderRequest randomOrderRequest() {
        final OrderRequest orderRequest = super.randomOrderRequest();
        orderRequest.setInstallmentHuabai(true);
        return orderRequest;
    }

//    @Override
//    protected String orderPageURI() {
//        return SystemService.wechatOrderURiHB;
//    }

    @Override
    protected WechatOrderPage openOrderPage() {
        return null;
    }
}
