package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.logistics.entity.Depot;
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
     * @param depot 全部发送按这个仓库
     */
    public void sendAllBy(Depot depot) {
//        printThisPage();
        inputSelect(form, "depot", depot.getName());
        submit.click();
    }
}
