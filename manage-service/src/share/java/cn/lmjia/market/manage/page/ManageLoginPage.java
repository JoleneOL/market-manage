package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.WebDriver;

/**
 * 客户管理
 * _loginManage.html
 *
 * @author CJ
 */
public class ManageLoginPage extends AbstractContentPage {
    public ManageLoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageLoginPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/loginManage");
        return instance.initPage(ManageLoginPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("客户管理");
    }
}
