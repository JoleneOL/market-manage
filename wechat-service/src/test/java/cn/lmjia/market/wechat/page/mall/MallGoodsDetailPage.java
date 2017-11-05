package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-28.
 */
public class MallGoodsDetailPage extends AbstractWechatPage {
    @FindBy(xpath = "//div[@class='goods-detail-ft']//a[contains(@class,'yellow-color')]")
    private WebElement openAddCartLink;
    @FindBy(xpath = "//div[@class='goods-detail-ft']//a[contains(@class,'red-color')]")
    private WebElement openBuyNowLink;
    @FindBy(id = "J_addCart")
    private WebElement addCartBtn;
    @FindBy(id = "J_buyNow")
    private WebElement buyNowBtn;
    @FindBy(className = "js-cartBtn")
    private WebElement toCart;

    public MallGoodsDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("商品详情");
    }

    public String getShareUrl() {
        return webDriver.findElement(By.tagName("body")).getAttribute("data-shareUrl");
    }

    /**
     * 校验某个商品
     *
     * @param mainGood 商品
     */
    public void validateGood(MainGood mainGood) {
        assertThat(webDriver.findElement(By.xpath("//div[@class=goods-info-wrap]")).getAttribute("data-goods-id"))
                .isEqualToIgnoringCase(mainGood.getId().toString());

    }

    public void clickBuyNow(){
        buyNowBtn.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        buyNowBtn.click();
    }

    /**
     * 校验类型相同的商品(假定只有一种规格)
     *
     * @param sameTypeGoodList
     */
    public void validateSameTypeGoods(MainGood mainGood, List<MainGood> sameTypeGoodList) {
        //遍历规格
        List<WebElement> propertyList = webDriver.findElements(By.xpath("//div[@class='sku-info']//ul/li"));
        assertThat(propertyList.size()).isGreaterThanOrEqualTo(1);
        propertyList.forEach(li -> {
            String propertyValue = li.getText();
            if (mainGood.getProduct().getPropertyNameValues().containsValue(propertyValue)) {
                assertThat(li.getAttribute("class").contains("active"));
            } else {
                MainGood propertyGood = sameTypeGoodList.stream().filter(p -> p.getProduct().getPropertyNameValues().containsValue(propertyValue)).findFirst().orElse(null);
                assertThat(propertyGood).isNotNull();
                if (propertyGood.getProduct().getStock() <= 0) {
                    assertThat(li.getAttribute("class").contains("disabled"));
                }
            }
        });
    }

    public void addGoodsToCard() {
        List<WebElement> propertyList = webDriver.findElements(By.xpath("//div[@class='sku-info']//ul/li"));
        assertThat(propertyList.size()).isGreaterThanOrEqualTo(1);
        propertyList.stream()
                .filter(li -> StringUtils.isEmpty(li.getAttribute("class")) || !li.getAttribute("class").contains("disabled"))
                .forEach(li -> {
                    openAddCartLink.click();
                    li.click();
                    assertThat(li.getAttribute("class").contains("active"));
                    addCartBtn.click();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                });
        toCart.click();
    }

    public void validateShareUrl(Long goodId, Long shareId) {
        String shareUrl = getShareUrl();
        assertThat(shareUrl).isNotEmpty();
        String goodAndId = shareUrl.substring(shareUrl.lastIndexOf("/") + 1);
        assertThat(goodAndId.indexOf("_")).isGreaterThanOrEqualTo(-1);
        assertThat(goodAndId.split("_")[0]).isEqualToIgnoringCase(String.valueOf(goodId));
        if (shareId != null) {
            assertThat(goodAndId.split("_")[1]).isEqualToIgnoringCase(String.valueOf(shareId));
        }
    }
}
