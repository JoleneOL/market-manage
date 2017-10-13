package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * 货款管理
 * <p>
 * _agentGoodAdvancePayment.html
 *
 * @author CJ
 */
public class ManageAgentGoodAdvancePaymentPage extends AbstractContentPage {
    public ManageAgentGoodAdvancePaymentPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageAgentGoodAdvancePaymentPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/agentGoodAdvancePaymentManage");
        return instance.initPage(ManageAgentGoodAdvancePaymentPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("预付货款管理");
    }

    /**
     * 拒绝这个合伙人的申请
     *
     * @param name
     */
    public void reject(String name) throws InterruptedException {
        WebElement targetRow = getTargetRow(name);
        targetRow.findElement(By.className("js-makeRefuse")).click();
        WebElement comment = webDriver.findElement(By.className("layui-layer-dialog")).findElement(By.id("J_makeRefuse_comment"));
        comment.clear();
        comment.sendKeys(RandomStringUtils.randomAlphabetic(5));
        clickLayerButton(0);
        Thread.sleep(500);
    }

    /**
     * 同意这个合伙人的申请
     *
     * @param name
     */
    public void approval(String name) throws InterruptedException {
        WebElement targetRow = getTargetRow(name);
        targetRow.findElement(By.className("js-makeApproval")).click();
        WebElement number = webDriver.findElement(By.className("layui-layer-dialog")).findElement(By.id("J_makeApproval_transactionRecordNumber"));
        number.clear();
        number.sendKeys(RandomStringUtils.randomNumeric(10));
        clickLayerButton(0);
        Thread.sleep(500);
    }

    private WebElement getTargetRow(String name) {
        waitForTable();
        return webDriver.findElement(By.id("withdrawTable"))
                .findElements(By.cssSelector("tr[role=row]"))
                .stream()
                .filter(row -> {
                    // 第二位是名字
                    final List<WebElement> elements = row.findElements(By.tagName("td"));
                    if (elements.size() < 4)
                        return false;
                    WebElement nameTd = elements.get(1);
                    return nameTd.getText().equals(name);
                })
                .findFirst().orElseThrow(() -> new IllegalStateException("找不到" + name + "的提现记录"));
    }
}
