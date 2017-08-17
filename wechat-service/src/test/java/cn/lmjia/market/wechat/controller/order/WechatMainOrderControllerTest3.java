package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.model.OrderRequest;

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
}
