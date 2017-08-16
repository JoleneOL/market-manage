package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 工厂发货至物流仓库
 * _delivery.html
 *
 * @author CJ
 */
public class ManageStorageDeliveryPage extends AbstractContentPage {

    @FindBy(name = "deliverQuantity")
    private WebElement deliverQuantity;
    @FindBy(css = "[type=submit]")
    private WebElement submit;

    public ManageStorageDeliveryPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("发货");
    }

    public ManageLogisticsPage submitAsAmount(String depotName, int amount) {
        inputSelect(webDriver.findElement(By.id("J_deliverForm")), "depot", depotName);
        deliverQuantity.clear();
        deliverQuantity.sendKeys(String.valueOf(amount));
        submit.click();
        return initPage(ManageLogisticsPage.class);
    }
}
