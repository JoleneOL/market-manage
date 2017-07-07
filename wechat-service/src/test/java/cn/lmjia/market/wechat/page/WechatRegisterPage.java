package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 其实只是信息处理页面
 * register.html
 *
 * @author CJ
 */
public class WechatRegisterPage extends AbstractWechatPage {
    private WebElement mobile;
    private WebElement authCode;
    private WebElement name;
    @FindBy(css = "button[type=submit]")
    private WebElement submit;
    @FindBy(id = "J_authCode")
    private WebElement sendButton;

    public WechatRegisterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("注册");
    }

    public void sendAuthCode(String mobile) {
        this.mobile.clear();
        this.mobile.sendKeys(mobile);
        sendButton.click();
    }

    public void submitSuccessAs(String name) {
        authCode.clear();
        authCode.sendKeys("1234");
        this.name.clear();
        this.name.sendKeys(name);
        submit.click();
    }
}
