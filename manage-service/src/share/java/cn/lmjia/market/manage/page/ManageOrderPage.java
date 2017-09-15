package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * _orderManage.html
 * 订单管理界面
 *
 * @author CJ
 */
public class ManageOrderPage extends AbstractContentPage {

    public ManageOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageOrderPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/orderManage");
        return instance.initPage(ManageOrderPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("用户订单");
    }

    /**
     * @param orderId
     * @return 某个订单的发货界面
     */
    public ManageDeliveryPage deliveryFor(long orderId) {
        webDriver.findElements(By.linkText("物流发货")).stream()
                .filter(element -> element.getAttribute("data-id").equals("" + orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有找到订单" + orderId + "的发货按钮"))
                .click();
        return initPage(ManageDeliveryPage.class);
    }
}
