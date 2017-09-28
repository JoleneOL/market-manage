package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 订单详情页
 * _orderDetail.html
 *
 * @author CJ
 */
public class ManageMainOrderDetailPage extends AbstractContentPage {
    public ManageMainOrderDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageMainOrderDetailPage of(SpringWebTest instance, WebDriver driver, long id) {
        driver.get("http://localhost/mainOrderDetail?id=" + id);
        return instance.initPage(ManageMainOrderDetailPage.class);
    }

    // 最新的或者指定id的
    public ManageShiftDetailPage shiftDetailFor(long id) {
        webDriver.findElements(By.className("logisticsDetailLink")).stream()
                .filter(element -> element.getAttribute("data-id").equals(String.valueOf(id)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到id为" + id + "的ShiftUnit"))
                .click();
        return initPage(ManageShiftDetailPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("订单详情");
    }
}
