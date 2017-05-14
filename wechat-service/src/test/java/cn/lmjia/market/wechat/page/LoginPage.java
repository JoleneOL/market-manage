package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * login.html
 * @author CJ
 */
public class LoginPage extends AbstractWechatPage {
    private WebElement username;
    private WebElement password;
    @FindBy(css = "[type=submit]")
    private WebElement submit;
    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("登录 - 微信管理平台");
    }

    public void login(String loginName, String rawPassword) {
        username.clear();
        username.sendKeys(loginName);
        password.clear();
        password.sendKeys(rawPassword);
        submit.click();
    }
}
