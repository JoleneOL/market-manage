package cn.lmjia.market.wechat.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 帮助中心的逻辑页面
 * 它的模版是/helpcenter/index.html
 */
public class HelpCenterPage extends AbstractWechatPage {
    public HelpCenterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("帮助");
    }

    /**
     *
     * @param title 確認頁面渲染出了這個標題的幫助
     */
    public void assertHasTopic(String title) {
        assertThat(webDriver.findElements(By.className("weui-cell")).stream()
                .map(webElement -> webElement.findElement(By.className("weui-cell__bd")))
                .map(WebElement::getText)
                .anyMatch(title::equalsIgnoreCase))
                .as("我们确保看到了这个标题的帮助")
                .isTrue();
    }
}
