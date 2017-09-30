package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Predicate;

/**
 * _salesmanManage.html
 *
 * @author CJ
 */
public class ManageSalesmanPage extends AbstractContentPage {
    public ManageSalesmanPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageSalesmanPage of(SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/manageSalesman");
        return instance.initPage(ManageSalesmanPage.class);
    }

    @Override
    public void validatePage() {
        assertTitle("销售人员管理");
    }

    public void selectNewSalesman(String mobile) {
        select2For("#loginInput", mobile, webElement -> webElement.getText().contains(mobile));
    }

    /**
     * 使用select2选择器
     *
     * @param selectSelector 相关select的cssSelector
     * @param input          输入内容
     * @param predicate      选择依赖
     */
    public void select2For(String selectSelector, String input, Predicate<WebElement> predicate) {
        final By resultsBy = By.className("select2-results__option");
        final By containerBy = By.className("select2-container--open");
        final By inputBy = By.cssSelector(".select2-search > input");
        final By loadMoreBy = By.className("select2-results__option--load-more");
        // 如果当前就打开了一个 那就不好弄了！
        if (webDriver.findElements(containerBy).stream().filter(WebElement::isDisplayed).count() > 0)
            throw new IllegalStateException("当前就打开了一个select2 选择器；点击其他地方关闭这个选择器的功能尚未开发呢。");


        WebElement selectHelper = webDriver.findElement(By.cssSelector(selectSelector + " + .select2-container"));
        selectHelper.findElement(By.className("select2-selection")).click();
        new WebDriverWait(webDriver, 2).until(ExpectedConditions.visibilityOfElementLocated(containerBy));

        WebElement inputElement = webDriver.findElement(inputBy);
        inputElement.clear();
        for (char c : input.toCharArray()) {
            inputElement.sendKeys("" + c);
//            printThisPage();
        }
//        inputElement.sendKeys(input);
        // 等待刷新
        WebDriverUtil.waitFor(webDriver, driver -> driver
                .findElements(resultsBy)
                .stream()
                .anyMatch(webElement -> !webElement.getAttribute("class").contains("loading-results")), 2);

        // select2-results__option--load-more
        int count = 0;
        while (count++ < 100) {
            WebElement target = webDriver.findElements(resultsBy).stream()
                    .filter(predicate)
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                target.click();
                return;
            }
            //寻找load more
//            new Actions(webDriver).
//            webDriver.findElement(loadMoreBy).click();
//            new WebDriverWait(webDriver,1).until(ExpectedConditions.visibilityOfElementLocated(loadMoreBy));
//            精准的做法是将它满满滚动
        }
//        webDriver.findElements(resultsBy).stream()
//                .filter(predicate)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("输入了" + input + "但没有找到期望的可选择结果"))
//                .click();
        throw new IllegalStateException("输入了" + input + "但没有找到期望的可选择结果");
    }

    /**
     * 新增为销售
     *
     * @param loginName 新的用户
     */
    public void addSalesman(String loginName) {
        selectNewSalesman(loginName);
        webDriver.findElement(By.linkText("新增为销售")).click();
    }
}
