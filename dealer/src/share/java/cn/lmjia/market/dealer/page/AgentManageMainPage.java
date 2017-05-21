package cn.lmjia.market.dealer.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import cn.lmjia.market.core.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * agentMain.html
 *
 * @author CJ
 */
public class AgentManageMainPage extends AbstractPage {
    public AgentManageMainPage(WebDriver webDriver) {
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
        // 先等到 dataTables_processing 不可见
//        if (!webDriver.findElements(By.className("dataTables_processing")).isEmpty())
        new WebDriverWait(webDriver, 2)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("dataTables_processing")));
//        webDriver.switchTo().
//        return initPage(pageClass, webDriver.switchTo().frame(webDriver.findElement(By.id("content"))));
        return initPage(pageClass);
    }
}
