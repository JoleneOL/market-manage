package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.AbstractPage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author CJ
 */
public abstract class AbstractWechatPage extends AbstractPage {

    public AbstractWechatPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 断言有弹出框，通常是我们的
     * $.toptip 导致的
     */
    public void assertHaveTooltip() {
        // 等 2秒 出现即可
        new WebDriverWait(webDriver, 2)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("bg-danger")));
//        assertThat(webDriver.findElements(By.className("bg-danger")).stream()
//                .filter(WebElement::isDisplayed)
//                .count())
//                .isGreaterThan(0);
    }

    /**
     * city-picker技术的随机地址填写
     *
     * @param form            form
     * @param addressName     address 字段的名称
     * @param fullAddressName 详细地址字段的名称
     */
    public void randomAddress(WebElement form, String addressName, String fullAddressName) {
        //
        // 点击地址弹出选择框
        form.findElement(By.name(addressName)).click();
        new WebDriverWait(webDriver, 2)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("col-province")));
        // col-province
        // 选择省级
        webDriver.findElement(By.className("col-province")).findElements(By.className("picker-item")).stream()
                .filter(WebElement::isDisplayed)
                .sorted(new SpringWebTest.RandomComparator())
                .findFirst()
                .ifPresent(provinceElement -> {
                    provinceElement.click();

                    webDriver.findElement(By.className("col-city")).findElements(By.className("picker-item")).stream()
                            .filter(WebElement::isDisplayed)
                            .sorted(new SpringWebTest.RandomComparator())
                            .findFirst()
                            .ifPresent(cityElement -> {
                                cityElement.click();

                                webDriver.findElement(By.className("col-district")).findElements(By.className("picker-item")).stream()
                                        .filter(WebElement::isDisplayed)
                                        .sorted(new SpringWebTest.RandomComparator())
                                        .findFirst()
                                        .ifPresent(WebElement::click);
                            });

                });

        // 点击完成
        webDriver.findElement(By.className("close-picker")).click();
//        new WebDriverWait(webDriver, 2)
//                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("col-province")));

        // 填写其他地址
        form.findElement(By.name(fullAddressName)).clear();
        form.findElement(By.name(fullAddressName)).sendKeys("其他地址" + RandomStringUtils.randomAlphabetic(10));
    }
}
