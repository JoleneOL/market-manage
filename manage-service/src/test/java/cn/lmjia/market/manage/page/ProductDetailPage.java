package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 货品详情
 * _productDetail.html
 *
 * @author CJ
 */
public class ProductDetailPage extends AbstractContentPage {
    public ProductDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("货品详情");
    }

    public ProductEditPage clickEdit() {
        webDriver.findElement(By.partialLinkText("修改")).click();
        return initPage(ProductEditPage.class);
    }
}
