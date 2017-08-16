package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 管理商品
 * _goodManage.html
 *
 * @author CJ
 */
public class ManageGoodPage extends AbstractContentPage {
    public ManageGoodPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("商品管理");
    }

    private WebElement firstVisibleElement(By by) {
        return webDriver.findElements(by).stream()
                .filter(WebElement::isDisplayed)
                .findFirst().orElse(null);
    }

    public GoodCreatePage clickNew() {
        webDriver.findElement(By.linkText("新增商品")).click();
        return initPage(GoodCreatePage.class);
    }

    /**
     * 点击第一个 禁用 按钮，
     *
     * @return 并且返回data-id
     */
    public String clickDisable() {
        final WebElement element = firstVisibleElement(By.className("js-offSale"));
        String id = element.getAttribute("data-id");
        element.click();
        // 确认
        clickLayerButton(0);
        waitForTable();
        return id;
    }

    public void clickEnable(String code) throws InterruptedException {
        webDriver.findElements(By.className("js-onSale")).stream()
                .filter(element -> element.getAttribute("data-id").equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到id为" + code + "的激活按钮"))
                .click();
        Thread.sleep(500L);
        waitForTable();
        Thread.sleep(500L);
        // 等待完成……
    }

    public GoodEditPage clickEditForFirstRow() {
        firstVisibleElement(By.className("js-checkInfo")).click();
        return initPage(GoodEditPage.class);
    }
}
