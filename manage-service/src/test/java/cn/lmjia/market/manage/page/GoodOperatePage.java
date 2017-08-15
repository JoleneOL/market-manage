package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 商品可编辑页面；
 * 可能是新建或者编辑
 * _goodOperate.html
 *
 * @author CJ
 */
public abstract class GoodOperatePage extends AbstractContentPage {

    @FindBy(name = "product")
    private WebElement product;
    @FindBy(name = "channel")
    private WebElement channel;

    public GoodOperatePage(WebDriver webDriver) {
        super(webDriver);
    }


    /**
     * @return 是否新建啊
     */
    protected abstract boolean isCreateNew();

    /**
     * 提交 并且设置渠道为空
     *
     * @return
     */
    public ManageGoodPage submitWithoutChannel() {
        if (product.isEnabled()) {
            inputSelect(webDriver.findElement(By.tagName("form")), "product", (x) -> true);
        }
        inputSelect(webDriver.findElement(By.tagName("form")), "channel", "无");
        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageGoodPage.class);
    }

    public ManageGoodPage submitWithChannel() {
        if (product.isEnabled()) {
            inputSelect(webDriver.findElement(By.tagName("form")), "product", (x) -> true);
        }
        inputSelect(webDriver.findElement(By.tagName("form")), "channel", (x) -> !"无".equals(x));
        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageGoodPage.class);
    }
}
