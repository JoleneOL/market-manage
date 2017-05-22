package cn.lmjia.market.dealer.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

/**
 * 代理商下单页面
 * orderPlace.html
 *
 * @author CJ
 */
public class AgentPlaceOrderPage extends AbstractContentPage {
    public AgentPlaceOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("用户订单 - 代理商后台管理");
    }
}
