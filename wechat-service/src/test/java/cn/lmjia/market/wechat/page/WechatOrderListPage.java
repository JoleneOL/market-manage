package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * 订单列表
 * orderList.html
 *
 * @author CJ
 */
public class WechatOrderListPage extends AbstractWechatPage {
    public WechatOrderListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("订单列表 - 微信管理平台");
    }
}
