package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        layerDialog((s, element) -> {
            WebElement orderNumber = element.findElement(By.name("orderNumber"));
            orderNumber.clear();
            orderNumber.sendKeys("号:" + RandomStringUtils.randomAlphabetic(7));
            WebElement company = element.findElement(By.name("company"));
            company.clear();
            company.sendKeys(RandomStringUtils.randomAlphabetic(3) + "公司");
            return true;
        });
        // 不应该再有弹出框
        assertInfo().isNull();
    }

}
