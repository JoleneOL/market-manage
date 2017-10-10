package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-25.
 */
public class MallSearchPage extends AbstractWechatPage {
    @FindBy(id = "J_searchInput")
    WebElement searchInput;
    public MallSearchPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("搜索");
    }

    public void searchGoods(MainGood good) throws InterruptedException {
        searchInput.clear();
        Thread.sleep(5);
        searchInput.sendKeys(good.getProduct().getName());
        Thread.sleep(5);
        searchInput.sendKeys(Keys.ENTER);
        Thread.sleep(1000);
        List<WebElement> resultItems = webDriver.findElement(By.id("J_resultContainer")).findElements(By.className("search-result_item"));
        assertThat(resultItems.size()).isGreaterThanOrEqualTo(1);
        WebElement goodItem = resultItems.stream()
                .filter(item->item.getAttribute("goods-id").equalsIgnoreCase(good.getId().toString()))
                .findFirst().orElse(null);
        assertThat(goodItem).isNotNull();
        assertThat(goodItem.findElement(By.className("goods-name")).getText()).isEqualTo(good.getProduct().getName());
    }
}
