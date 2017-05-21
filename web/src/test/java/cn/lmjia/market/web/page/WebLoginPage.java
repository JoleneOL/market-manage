package cn.lmjia.market.web.page;

import cn.lmjia.market.core.pages.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * login.html
 *
 * @author CJ
 */
public class WebLoginPage extends AbstractPage {

    private WebElement username;
    private WebElement password;
    @FindBy(id = "loginButton")
    private WebElement loginButton;

    public WebLoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("登录 - 代理商后台管理");
    }

    /**
     * 登录
     *
     * @param name     用户名
     * @param password 密码
     */
    public void login(String name, String password) {
        this.username.clear();
        this.username.sendKeys(name);
        this.password.clear();
        this.password.sendKeys(password);
        loginButton.click();
    }
}
