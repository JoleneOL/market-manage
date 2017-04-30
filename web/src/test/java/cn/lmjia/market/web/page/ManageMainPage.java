package cn.lmjia.market.web.page;

import org.openqa.selenium.WebDriver;

/**
 * main.html
 *
 * @author CJ
 */
public class ManageMainPage extends AbstractPage {
    public ManageMainPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("代理商后台管理");
    }
}
