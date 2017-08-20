package cn.lmjia.market.manage.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class GoodCreatePage extends GoodOperatePage {
    public GoodCreatePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected boolean isCreateNew() {
        return true;
    }

    @Override
    public void validatePage() {
        assertTitle("新增商品");
    }
}
