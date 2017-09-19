package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * 管理货品
 * _productManage.html
 *
 * @author CJ
 */
public class ManageProductPage extends AbstractContentPage {
    public ManageProductPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("货品管理");
    }

    /**
     * 点击推送给日日顺 第一条记录
     */
    public void clickPushHaierForFirstRow() {
        firstVisibleElement(By.partialLinkText("推送给日日顺")).click();
    }

    private WebElement firstVisibleElement(By by) {
        return webDriver.findElements(by).stream()
                .filter(WebElement::isDisplayed)
                .findFirst().orElse(null);
    }

    public ProductDetailPage clickViewForFirstRow() {
        firstVisibleElement(By.className("js-checkInfo")).click();
        return initPage(ProductDetailPage.class);
    }

    public ProductCreatePage clickNew() {
        //随机选择一个货品类型
//        webDriver.findElement(By.id("J-productType"))
//                .findElements(By.tagName("option"))
//                .stream().max(new SpringWebTest.RandomComparator())
//                .orElseThrow(() -> new IllegalStateException("找不到可选的货品类型"))
//                .click();
        new Select(webDriver.findElement(By.id("J-productType"))).selectByVisibleText("台式净水器");
        webDriver.findElement(By.linkText("新增货品")).click();
        return initPage(ProductCreatePage.class);
    }

    /**
     * 点击第一个 禁用 按钮，
     *
     * @return 并且返回data-id
     */
    public String clickDisable() {
        final WebElement element = firstVisibleElement(By.className("js-delete"));
        String id = element.getAttribute("data-id");
        element.click();
        // 确认
        clickLayerButton(0);
        waitForTable();
        return id;
    }

    public void clickEnable(String code) throws InterruptedException {
        webDriver.findElements(By.className("js-active")).stream()
                .filter(element -> element.getAttribute("data-id").equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到id为" + code + "的激活按钮"))
                .click();
        Thread.sleep(500L);
        waitForTable();
        Thread.sleep(500L);
        // 等待完成……
    }
}
