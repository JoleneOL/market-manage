package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import me.jiangcai.logistics.entity.PropertyValue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-26.
 */
public class MallTagDetailPage extends AbstractWechatPage {

    public MallTagDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("分类详情");
        new WebDriverWait(webDriver, 1)
                .until(ExpectedConditions.invisibilityOf(webDriver.findElement(By.className("weui-loadmore__tips"))));
    }

    public void validateGoods(List<MainGood> goodsList) {
        List<WebElement> searchResultItems = webDriver.findElements(By.xpath("//a[@class='search-result_item']"));
        assertThat(searchResultItems).isNotEmpty();
        goodsList.forEach(goods -> {
            assertThat(searchResultItems.stream()
                    .filter(item -> item.getAttribute("goods-id").equalsIgnoreCase(goods.getId().toString()))
                    .findFirst().orElse(null))
                    .isNotNull();
        });
    }

    public void clickTagOrPropertyValue(String option) throws InterruptedException {
        if (!StringUtils.isEmpty(option)) {
            webDriver.findElement(By.xpath("//li[@data-id='" + option + "']"))
                    .click();
        } else {
            webDriver.findElement(By.xpath("//div[contains(@class, 'filter-tag')]"))
                    .click();
            webDriver.findElement(By.xpath("//ul[contains(@class,'js-tagDrop')]/li[1]"))
                    .click();
            Thread.sleep(500);
            webDriver.findElement(By.xpath("//div[contains(@class, 'filter-prop')]"))
                    .click();
            webDriver.findElement(By.xpath("//ul[contains(@class,'js-propDrop')]/li[1]"))
                    .click();
        }
        Thread.sleep(500);
    }
}
