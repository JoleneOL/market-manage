package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.BiFunction;

/**
 * 发货页面
 * _orderDelivery.html
 *
 * @author CJ
 */
public class ManageDeliveryPage extends AbstractContentPage {
    @FindBy(id = "J_form")
    private WebElement form;
    @FindBy(id = "J_delivery")
    private WebElement submit;

    public ManageDeliveryPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("订单发货");
    }

    /**
     * 如果提示输入快递单号，就随便输入一个
     *
     * @param depot 全部发送按这个仓库
     */
    public void sendAllBy(String depot) {
//        printThisPage();
        inputSelect(form, "depot", depot);
        // 如果都没有值 则都设置为1
        if (webDriver.findElements(By.cssSelector("input[name=goods]")).stream()
                .filter(element -> element.getAttribute("value") != null && element.getAttribute("value").length() > 0)
                .count() == 0) {
            webDriver.findElements(By.cssSelector("input[name=goods]")).stream()
                    .max(new SpringWebTest.RandomComparator())
                    .ifPresent(element -> element.sendKeys("1"));
        }
        submit.click();
        layerPrompt((s, element) -> {
            WebElement input = element.findElement(By.tagName("input"));
            input.clear();
            input.sendKeys("号:" + RandomStringUtils.randomAlphabetic(7));
            return true;
        });
        // 不应该再有弹出框
        assertInfo().isNull();
    }

    /**
     * 检查下是否有弹出框，有的话function就会被执行
     *
     * @param function 参数分别为弹出窗标题，整个弹出界面的div；如果返回true则表示输入，返回false就直接关闭
     */
    private void layerPrompt(BiFunction<String, WebElement, Boolean> function) {
        //
        try {
            final By locator = By.className("layui-layer-prompt");
            new WebDriverWait(webDriver, 1)
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            WebElement div = webDriver.findElements(locator).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
            if (function.apply(div.findElement(By.className("layui-layer-title")).getText(), div)) {
                div.findElement(By.className("layui-layer-btn0")).click();
            } else {
                div.findElement(By.className("layui-layer-btn1")).click();
            }
        } catch (TimeoutException ignored) {
        }
    }
}
