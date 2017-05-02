package cn.lmjia.market.web.page;

import me.jiangcai.lib.test.page.WebDriverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

/**
 * @author CJ
 */
public class AgentManagePage extends AbstractContentPage {
    private static final Log log = LogFactory.getLog(AgentManagePage.class);

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
        WebDriverUtil.waitFor(webDriver, webDriver
                -> !webDriver.findElements(By.className("js-checkUser")).isEmpty(), 1);
        return webDriver.findElements(By.className("js-checkUser")).stream()
                .filter(WebElement::isDisplayed);
    }
}
