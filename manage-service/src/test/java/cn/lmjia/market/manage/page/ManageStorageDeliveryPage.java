package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

/**
 * 工厂发货至物流仓库
 * _
 *
 * @author CJ
 */
public class ManageStorageDeliveryPage extends AbstractContentPage {
    public ManageStorageDeliveryPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("发货");
    }
}
