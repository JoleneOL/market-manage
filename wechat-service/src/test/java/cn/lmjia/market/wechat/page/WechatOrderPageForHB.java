package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author CJ
 */
public class WechatOrderPageForHB extends WechatOrderPage {

    @FindBy(id = "J_installment")
    private WebElement checkBox;

    public WechatOrderPageForHB(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void typeOtherInformation() {
        super.typeOtherInformation();
        if (!checkBox.isSelected())
            checkBox.click();
    }
}
