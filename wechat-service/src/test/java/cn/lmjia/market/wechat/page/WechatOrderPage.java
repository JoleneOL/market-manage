package cn.lmjia.market.wechat.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 下单页面
 * <p>
 * orderPlace.html
 *
 * @author CJ
 */
public class WechatOrderPage extends AbstractWechatPage {

    @FindBy(id = "J_orderTotal")
    private WebElement totalPrice;

    public WechatOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的下单");
    }

    /**
     * 所有商品价格都为price
     *
     * @param price 特定价格
     */
    public void allPriced(BigDecimal price) {
        price = price.setScale(0, BigDecimal.ROUND_HALF_UP);
        if (!"1".equals(webDriver.findElement(By.id("J_goodsAmount")).getAttribute("value")))
            inputText(webDriver.findElement(By.id("J_form")), "amount", "1");
        WebElement select = webDriver.findElement(By.id("J_goodsType"));

        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isEnabled()) {
                option.click();
                // 包含该取整值
                assertThat(totalPrice.getText())
                        .startsWith(price.toString());
            }
        }
    }
}
