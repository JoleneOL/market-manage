package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-28.
 */
public class MallCartPage extends AbstractWechatPage {

    @FindBy(id = "J_allCheck")
    private WebElement checkAll;
    @FindBy(id = "js-editBtn")
    private WebElement editBtn;
    @FindBy(id = "J_settlementBtn")
    private WebElement settlementBtn;

    public MallCartPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("购物车");
    }

    public void validateGoods(List<MainGood> cartGoods){
        List<WebElement> goodElements = webDriver.findElements(By.xpath("//div[@class='mall-cart-list']/div[@class='cart-group']"));
        assertThat(goodElements.size()).isEqualTo(cartGoods.size());
        goodElements.forEach(element->{
            Long goodsId = Long.valueOf(element.getAttribute("data-goods-id"));
            MainGood mainGood = cartGoods.stream().filter(p->p.getId().equals(goodsId)).findFirst().orElse(null);
            assertThat(mainGood).isNotNull();
        });
    }

    public void deleteAllCart(){
        List<WebElement> goodElements = webDriver.findElements(By.xpath("//div[@class='mall-cart-list']/div[@class='cart-group']"));
        if(CollectionUtils.isEmpty(goodElements))
            return;
        //点一下编辑
        editBtn.click();
        goodElements.forEach(element->{
            element.findElement(By.className("js-deleteCart")).click();
            new WebDriverWait(webDriver, 1)
                    .until(ExpectedConditions.visibilityOf(webDriver.findElement(By.className("weui-dialog"))));
            webDriver.findElement(By.xpath("//div[contains(@class,'weui-dialog')]//a[contains(@class,'primary')]")).click();
        });
        assertThat(CollectionUtils.isEmpty(goodElements));
    }

    public void clickCheckAll(){
        checkAll.click();
    }

    public void clickSettlement(){
        settlementBtn.click();
    }
}
