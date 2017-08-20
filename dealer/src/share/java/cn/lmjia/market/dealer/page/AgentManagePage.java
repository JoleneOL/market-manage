package cn.lmjia.market.dealer.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.stream.Stream;

/**
 * agentManage.html
 *
 * @author CJ
 */
public class AgentManagePage extends AbstractContentPage {

    public AgentManagePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("代理商管理 - 代理商后台管理");
    }

    /**
     * @return 所有查看详情的按钮的流
     */
    public Stream<WebElement> buttonsForDetail() {
        new WebDriverWait(webDriver, 1)
                .until(ExpectedConditions.elementToBeClickable(By.className("js-checkUser")));
//        WebDriverUtil.waitFor(webDriver, webDriver
//                -> buttonsForDetail(webDriver).count() > 0, 1);
        return buttonsForDetail(webDriver);
    }

    private Stream<WebElement> buttonsForDetail(WebDriver webDriver) {

        return webDriver.findElements(By.className("js-checkUser")).stream()
                .peek(System.out::println)
                .filter(WebElement::isEnabled)
                ;
    }
}
