package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.WebDriver;

/**
 * 提现管理
 *
 * @author CJ
 */
public class ManageWithdrawPage extends AbstractContentPage {
    public ManageWithdrawPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageWithdrawPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/withdrawManage");
        return instance.initPage(ManageWithdrawPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("提现管理");
    }
}
