package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.wechat.page.AbstractWechatPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by helloztt on 2017-09-28.
 */
public class MallOrderPlacePage extends AbstractWechatPage {
    @FindBy(id = "J_goodsList")
    private WebElement goodListRegion;
    @FindBy(name = "name")
    private WebElement name;
    @FindBy(name = "mobile")
    private WebElement mobile;

    public MallOrderPlacePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的下单");
    }

    public void submitOrder(){
        // 其他信息
        name.clear();
        name.sendKeys("W客户" + RandomStringUtils.randomAlphabetic(6));
        randomAddress(webDriver.findElement(By.id("J_form")), "address", "fullAddress");
        // 手机号码
        mobile.clear();
        mobile.sendKeys(SpringWebTest.randomAllMobile());
        webDriver.findElement(By.id("J_submitBtn")).click();
    }
}
