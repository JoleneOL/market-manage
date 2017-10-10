package cn.lmjia.market.wechat.page.mall;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-22.
 */

public class MallIndexPage extends AbstractWechatPage {
    @FindBy(className = "js-cartBtn")
    private WebElement cartLink;
    public MallIndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("商城");
    }

    /**
     * 校验滚图
     *
     * @param imgTags
     */
    public void validatePageWithImgTag(List<Tag> imgTags) {
        List<WebElement> imgDivs = webDriver.findElements(By.className("swiper-slide"));
        assertThat(imgDivs.size()).isEqualTo(imgTags.size());
        imgDivs.forEach(div -> {
            String tagName = div.getAttribute("tagName");
            WebElement img = div.findElement(By.tagName("img"));
            assertThat(img.getAttribute("src").contains(
                    imgTags.stream().filter(tag -> tag.getName().equalsIgnoreCase(tagName)).findFirst().get().getIcon()));
        });
    }

    public void validatePageWithSearch(List<Tag> searchTags) {
        List<WebElement> searchDivs = webDriver.findElement(By.className("mall-tagBanner"))
                .findElements(By.className("tag-list-item"));
        assertThat(searchDivs.size()).isEqualTo(searchTags.size());
        searchDivs.forEach(div -> {
            WebElement img = div.findElement(By.tagName("img"));
            WebElement tagName = div.findElement(By.tagName("p"));
            assertThat(img.getAttribute("src").contains(
                    searchTags.stream().filter(tag -> tag.getName().equalsIgnoreCase(tagName.getText())).findFirst().get().getIcon()
            ));
        });
    }

    public void validatePageWithList(List<Tag> listTags, MainGood mainGood) {
        List<WebElement> listDivs = webDriver.findElement(By.className("mall-tagList"))
                .findElements(By.className("tag-list"));
        assertThat(listDivs.size()).isEqualTo(listTags.size());
        listDivs.forEach(div -> {
            WebElement tagName = div.findElement(By.tagName("h4"));
            List<WebElement> goodsList = div.findElements(By.className("tag-list-item"));
            assertThat(goodsList.size()).isGreaterThanOrEqualTo(1);
            WebElement goodDiv = goodsList.stream()
                    .filter(good -> good.getAttribute("data-goods-id").equals(mainGood.getId().toString()))
                    .findFirst().orElse(null);
            assertThat(goodDiv).isNotNull();

        });
    }

    public void clickSearch() {
        webDriver.findElement(By.className("m-search")).click();
    }

    public void clickTagSearch(Tag searchTag) {
        webDriver.findElement(By.xpath("//div[@class='mall-tagBanner']//a[@tag-name='" + searchTag.getName() + "']"))
                .click();
    }

    public void clickGoods(Tag listTag, MainGood mainGood) {
        webDriver.findElement(By.xpath("//div[@data-tag-name='" + listTag.getName() + "']" +
                "//div[@class='tag-list-item'][@data-goods-id='" + mainGood.getId() + "']" +
                "/a")).click();
    }

    public void clickCart(){
        cartLink.click();
    }
}
