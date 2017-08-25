package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;
import java.util.List;

/**
 * 货品可编辑页面；
 * 可能是新建或者编辑
 * _productOperate.html
 *
 * @author CJ
 */
public abstract class ProductOperatePage extends AbstractContentPage {
    public static final String[] FieldForOnceLock = new String[]{
//            "productName", "type",
            "productBrand", "mainCategory", "SKU", "unit", "length", "width", "height", "weight"
            , "productSummary"
    };
    @FindBy(name = "productName")
    private WebElement productName;
    @FindBy(name = "type")
    private WebElement type;
    @FindBy(name = "productPrice")
    private WebElement productPrice;
    @FindBy(name = "serviceCharge")
    private WebElement serviceCharge;
    @FindBy(name = "productBrand")
    private WebElement productBrand;
    @FindBy(name = "mainCategory")
    private WebElement mainCategory;
    @FindBy(name = "SKU")
    private WebElement SKU;
    @FindBy(name = "unit")
    private WebElement unit;
    @FindBy(name = "length")
    private WebElement length;
    @FindBy(name = "width")
    private WebElement width;
    @FindBy(name = "height")
    private WebElement height;
    @FindBy(name = "weight")
    private WebElement weight;
    @FindBy(name = "productSummary")
    private WebElement productSummary;
//    @FindBy(name = "richDescription")
//    private WebElement richDescription;
    @FindBy(name = "planSellOutDate")
    private WebElement planSellOutDate;

    public ProductOperatePage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 输入字段，除了这些以外
     *
     * @param fieldNames
     * @return 提交
     */
    public ManageProductPage submitWithout(String... fieldNames) {

        // 本身就必须的 productName,type,productPrice,serviceCharge
        // ok 那么选填的内容大概是  richDescription
        // 一次编辑锁定的有 productName,type,productBrand,mainCategory,SKU,unit,length,width,height,weight,productSummary

        // 新建必须都是可编辑的
        tryInput(productName, RandomStringUtils.randomAlphabetic(5) + "货品");
        tryInput(type, RandomStringUtils.randomAlphabetic(10));
        tryInput(productPrice, "501.11");
        tryInput(serviceCharge, "0");

        // 不在里面的就尽管输入了
        List<String> fields = Arrays.asList(fieldNames);
        if (!fields.contains("productBrand"))
            tryInput(productBrand, RandomStringUtils.randomAlphabetic(5) + "品牌");
        if (!fields.contains("mainCategory"))
            tryInput(mainCategory, RandomStringUtils.randomAlphabetic(5) + "类目");
        if (!fields.contains("SKU"))
            tryInput(SKU, RandomStringUtils.randomAlphabetic(5) + "SS");
        if (!fields.contains("unit"))
            tryInput(unit, RandomStringUtils.randomAlphabetic(2));
        if (!fields.contains("productSummary"))
            tryInput(productSummary, RandomStringUtils.randomAlphabetic(20) + "哈哈");

        if (!fields.contains("length"))
            tryInput(length, "500");
        if (!fields.contains("width"))
            tryInput(width, "500");
        if (!fields.contains("height"))
            tryInput(height, "500");
        if (!fields.contains("weight"))
            tryInput(weight, "500");
        if (!fields.contains("planSellOutDate")) {
            // TODO: 2017/8/20 这里应该如何模拟时间控件点击事件？
        }

        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageProductPage.class);
    }

    private void tryInput(WebElement field, String text) {
        if (isCreateNew()) {
            field.clear();
            field.sendKeys(text);
        } else {
            // 应该是readonly
            if (field.isEnabled() && field.getAttribute("readonly") == null) {
                field.clear();
                field.sendKeys(text);
            }
        }
    }

    /**
     * @return 是否新建啊
     */
    protected abstract boolean isCreateNew();
}
