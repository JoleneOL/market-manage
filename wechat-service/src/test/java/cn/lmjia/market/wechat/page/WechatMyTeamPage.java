package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class WechatMyTeamPage extends AbstractWechatPage {
    public WechatMyTeamPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的团队 - 微信管理平台");
    }
}
