package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * _salesmanManage.html
 *
 * @author CJ
 */
public class ManageSalesmanPage extends AbstractContentPage {
    public ManageSalesmanPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageSalesmanPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/manageSalesman");
        return instance.initPage(ManageSalesmanPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("销售人员管理");
    }

    public void selectNewSalesman(String mobile) {
        select2For("#loginInput", mobile, webElement -> webElement.getText().contains(mobile));
    }

    /**
     * 新增为销售
     *
     * @param loginName 新的用户
     */
    public void addSalesman(String loginName) {
        selectNewSalesman(loginName);
        webDriver.findElement(By.linkText("新增为销售")).click();
    }
}
