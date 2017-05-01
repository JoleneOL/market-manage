package cn.lmjia.market.web.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * main.html
 *
 * @author CJ
 */
public class ManageMainPage extends AbstractPage {
    public ManageMainPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("代理商后台管理");
    }

    public void selectMenu(String className) {
        webDriver.switchTo().parentFrame();
        webDriver.findElements(By.tagName("a")).stream()
                .filter(element -> !element.findElements(By.className(className)).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到" + className))
                .click();
    }

    public <T extends AbstractContextPage> T currentContext(Class<T> pageClass) {
        webDriver.switchTo().frame("content");
        return initPage(pageClass);
    }
}
