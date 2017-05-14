package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public abstract class AbstractWechatPage extends AbstractPage {

    public AbstractWechatPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 断言有弹出框，通常是我们的
     * $.toptip 导致的
     */
    public void assertHaveTooltip() {
        assertThat(webDriver.findElements(By.className("bg-danger")).stream()
                .filter(WebElement::isDisplayed)
                .count())
                .isGreaterThan(0);
    }
}
