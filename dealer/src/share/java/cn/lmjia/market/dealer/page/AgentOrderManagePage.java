package cn.lmjia.market.dealer.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

/**
 * 代理商管理订单的页面
 * orderManage.html
 *
 * @author CJ
 */
public class AgentOrderManagePage extends AbstractContentPage {
    public AgentOrderManagePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("用户订单 - 代理商后台管理");
    }
}
