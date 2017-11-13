package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.WebDriver;

public class ManageHelpCenterPage extends AbstractContentPage {

    public ManageHelpCenterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("帮助中心");
    }

    public void assertHasTopic(String title) {
        printHtml();
    }
}
