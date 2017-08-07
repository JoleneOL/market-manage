package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        // 等待完成……
        return id;
    }

    public void clickEnable(String code) {
        webDriver.findElements(By.className("js-active")).stream()
                .filter(element -> element.getAttribute("data-id").equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到id为" + code + "的激活按钮"))
                .click();
        // 等待完成……
    }
}
