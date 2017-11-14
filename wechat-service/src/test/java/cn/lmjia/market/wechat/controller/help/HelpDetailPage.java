package cn.lmjia.market.wechat.controller.help;

import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class HelpDetailPage extends AbstractWechatPage {
    public HelpDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("帮助详情");
    }


    public void asssertHasTopic(String title) {
        assertThat(webDriver.findElements(By.className("weui-article")).stream()
                .map(webElement -> webElement.findElement(By.tagName("h1")))
                .map(WebElement::getText)
                .anyMatch(title::equalsIgnoreCase))
                .as("我们确保看到了这个标题的帮助的详情")
                .isTrue();
    }
}
