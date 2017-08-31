package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 提现校验页面
 * withdrawVerify.html
 *
 * @author CJ
 */
public class WechatWithdrawVerifyPage extends AbstractWechatPage {

    @FindBy(name = "authCode")
    private WebElement authCode;
    @FindBy(css = "button[type=submit]")
    private WebElement submit;

    public WechatWithdrawVerifyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("验证手机号码");
    }

    /**
     * 成功提现 可以看到 提现申请成功
     *
     * @param code code
     */
    public void submitCode(String code) {
        authCode.clear();
        authCode.sendKeys(code);
        submit.click();
        assertThat(webDriver.getTitle())
                .isEqualTo("提现申请成功");
    }
}
