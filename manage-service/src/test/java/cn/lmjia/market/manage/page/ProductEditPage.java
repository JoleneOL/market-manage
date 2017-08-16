package cn.lmjia.market.manage.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class ProductEditPage extends ProductOperatePage {
    public ProductEditPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected boolean isCreateNew() {
        return false;
    }

    @Override
    public void validatePage() {
        assertTitle("编辑货品");
    }
}
