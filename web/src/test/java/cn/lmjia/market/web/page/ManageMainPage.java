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

    /**
     * 点击菜单
     *
     * @param className 菜单样式(class)
     */
    public void selectMenu(String className) {
//        webDriver = webDriver.switchTo().parentFrame();
        // 基于获取子frame的困难； 我们此处在原地址打开新窗口
        String targetSrc = webDriver.findElements(By.tagName("a")).stream()
                .filter(element -> !element.findElements(By.className(className)).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到" + className))
//                .click();
                .getAttribute("href");
        webDriver.get(targetSrc);
    }

    public <T extends AbstractContentPage> T currentContext(Class<T> pageClass) {
//        webDriver.switchTo().
//        return initPage(pageClass, webDriver.switchTo().frame(webDriver.findElement(By.id("content"))));
        return initPage(pageClass);
    }
}
