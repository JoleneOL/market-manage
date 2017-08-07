package cn.lmjia.market.core.pages;

import me.jiangcai.lib.test.page.WebDriverUtil;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public abstract class AbstractPage extends me.jiangcai.lib.test.page.AbstractPage {

    public AbstractPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 会自动等待2s为了 等消息的出现；即使不出现也不会报错
     *
     * @return 断言弹出的消息
     */
    public AbstractCharSequenceAssert<?, String> assertInfo() {
        try {
            WebDriverUtil.waitFor(webDriver, driver -> driver.findElements(By.className("layui-layer-content")).stream()
                    .filter(WebElement::isDisplayed).count() > 0, 2);
        } catch (TimeoutException ignored) {
        }
        WebElement target = webDriver.findElements(By.className("layui-layer-content")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst().orElse(null);
        if (target == null)
            return assertThat((String) null);
        return assertThat(target.getText());
    }

    /**
     * 等待table 载入完成
     */
    public void waitForTable() {
        new WebDriverWait(webDriver, 3)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("dataTables_processing")));
        // 顺便移除
        webDriver.findElements(By.className("dataTable")).forEach(this::makeVisible);
    }

    public void makeVisible(WebElement element) {
        String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
        ((JavascriptExecutor) webDriver).executeScript(js, element);
    }

    /**
     * 点击某一个layer弹出的按钮
     *
     * @param id 第几个 从0开始
     */
    public void clickLayerButton(int id) {
        webDriver.findElement(By.className("layui-layer-btn" + id))
                .click();
        new WebDriverWait(webDriver, 3)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("layui-layer-btn" + id)));
    }
}
