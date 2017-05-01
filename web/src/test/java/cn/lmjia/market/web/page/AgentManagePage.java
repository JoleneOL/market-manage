package cn.lmjia.market.web.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class AgentManagePage extends AbstractContextPage {
    public AgentManagePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("代理商管理 - 代理商后台管理");
    }
}
