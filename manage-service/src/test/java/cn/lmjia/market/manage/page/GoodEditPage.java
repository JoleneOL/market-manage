package cn.lmjia.market.manage.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class GoodEditPage extends GoodOperatePage {
    public GoodEditPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected boolean isCreateNew() {
        return false;
    }

    @Override
    public void validatePage() {
        assertTitle("编辑商品");
    }
}
