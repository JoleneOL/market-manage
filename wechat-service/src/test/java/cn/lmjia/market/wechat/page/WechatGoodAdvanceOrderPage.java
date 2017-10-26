package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.WebDriver;

/**
 * 预付货款下单界面
 *
 * @author CJ
 */
public class WechatGoodAdvanceOrderPage extends WechatOrderPage {

    public WechatGoodAdvanceOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static WechatGoodAdvanceOrderPage of(SpringWebTest instance, WebDriver driver) {
        // 首先打开
//        driver.get("http://localhost" + SystemService.goodAdvanceOrderList);
//        driver.findElement(By.className("weui-btn-area"))
//                .click();
        driver.get("http://localhost/wechatAgentPrepaymentOrder");
        return instance.initPage(WechatGoodAdvanceOrderPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("批货下单");
    }
}
