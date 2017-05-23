package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * 微信我的页面
 *
 * @author CJ
 */
public class WechatMyPage extends AbstractWechatPage {
    public WechatMyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的 - 微信管理平台");
    }
}
