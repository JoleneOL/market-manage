package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * login.html
 *
 * @author CJ
 */
public class LoginPage extends AbstractWechatPage {
    private WebElement username;
    private WebElement password;
    @FindBy(css = "[form=J_passwordForm]")
    private WebElement passwordSubmit;
    @FindBy(linkText = "手机号快捷登录")
    private WebElement messageLogin;
    private WebElement mobile;
    private WebElement authCode;
    @FindBy(id = "J_authCode")
    private WebElement sendButton;
    @FindBy(css = "[form=J_messageForm]")
    private WebElement messageSubmit;

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
        passwordSubmit.click();
    }

    public void sendAuthCode(String mobile) {
        if (!this.mobile.isDisplayed())
            messageLogin.click();
        this.mobile.clear();
        this.mobile.sendKeys(mobile);
        sendButton.click();
    }

    public void loginWithAuthCode(String code) {
        this.authCode.clear();
        this.authCode.sendKeys(code);
        messageSubmit.click();
    }
}
