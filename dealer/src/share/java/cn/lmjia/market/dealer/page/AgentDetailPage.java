package cn.lmjia.market.dealer.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

/**
 * agentDetail.html
 *
 * @author CJ
 */
public class AgentDetailPage extends AbstractContentPage {

    public AgentDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("代理商详情 - 代理商后台管理");
    }

    public AgentManagePage back() {
        clickBreadcrumb();
        return initPage(AgentManagePage.class);
    }

}
