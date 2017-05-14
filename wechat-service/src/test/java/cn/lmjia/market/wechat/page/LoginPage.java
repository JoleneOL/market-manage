package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class LoginPage extends AbstractWechatPage {
    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("登录 - 微信管理平台");
    }
}
