package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * _storageManage.html
 * 仓储管理
 *
 * @author CJ
 */
public class ManageStoragePage extends AbstractContentPage {
    public ManageStoragePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("仓储管理");
    }

    /**
     * 点击发货
     *
     * @return
     */
    public ManageStorageDeliveryPage clickDelivery() {
        new WebDriverWait(webDriver, 1).until(ExpectedConditions.elementToBeClickable(By.id("J_DeliveryLink")));
        webDriver.findElement(By.id("J_DeliveryLink")).click();
        return initPage(ManageStorageDeliveryPage.class);
    }
}
