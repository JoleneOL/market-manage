package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 物流订单详情页
 * _logisticsDetail.html
 *
 * @author CJ
 */
public class ManageShiftDetailPage extends AbstractContentPage {
    public ManageShiftDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("物流详情");
    }

    public void mockReject() {
        forLink("已被取消");
    }

    private void forLink(String linkText) {
        webDriver.findElement(By.partialLinkText(linkText)).click();
        layerDialog((s, e) -> true);
    }

    public void mockSuccess() {
        forLink("已成功送达");
    }
}
