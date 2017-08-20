package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

/**
 * 物流管理页面
 * _logisticsManage.html
 *
 * @author CJ
 */
public class ManageLogisticsPage extends AbstractContentPage {
    public ManageLogisticsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("物流管理");
    }
}
