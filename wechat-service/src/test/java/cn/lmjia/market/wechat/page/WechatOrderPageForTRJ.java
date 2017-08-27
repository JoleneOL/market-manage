package cn.lmjia.market.wechat.page;

import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author CJ
 */
public class WechatOrderPageForTRJ extends WechatOrderPage {

    @FindBy(name = "authorising")
    private WebElement authorising;
    @FindBy(name = "idNumber")
    private WebElement idNumber;

    public WechatOrderPageForTRJ(WebDriver webDriver) {
        super(webDriver);
    }

    public static WechatOrderPageForTRJ of(SpringWebTest test, WebDriver driver) {
        driver.get("http://localhost" + TRJEnhanceConfig.TRJOrderURI);
        return test.initPage(WechatOrderPageForTRJ.class);
    }

    public void submitAuthorising(String authorising, String code) {
        this.authorising.clear();
        this.authorising.sendKeys(authorising);

        idNumber.clear();
        idNumber.sendKeys(code);
    }
}
