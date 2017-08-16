package cn.lmjia.market.manage.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class ProductCreatePage extends ProductOperatePage {
    public ProductCreatePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected boolean isCreateNew() {
        return true;
    }

    @Override
    public void validatePage() {
        assertTitle("新增货品");
    }
}
