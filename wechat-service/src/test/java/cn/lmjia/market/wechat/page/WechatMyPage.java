package cn.lmjia.market.wechat.page;

import cn.lmjia.market.wechat.model.MemberInfo;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.WebDriverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 微信我的页面
 *
 * @author CJ
 */
public class WechatMyPage extends AbstractWechatPage {
    private static final Log log = LogFactory.getLog(WechatMyPage.class);

    public WechatMyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的");
    }

    /**
     * @return 断言可提现金额
     */
    public AbstractBigDecimalAssert<?> assertWithdrawAble() {
        switchToCommission();
        String text = webDriver.findElement(By.id("WithdrawAbleAmount")).getText().replaceAll("￥", "").replaceAll(",", "");
        return assertThat(new BigDecimal(text));
    }

    /**
     * @return 去提现页面
     */
    public WechatWithdrawPage toWithdrawPage() {
        switchToCommission();
        webDriver.findElement(By.linkText("我要提现")).click();
        return initPage(WechatWithdrawPage.class);
    }

    /**
     * 切换到资产界面
     */
    private void switchToCommission() {
        webDriver.findElement(By.linkText("资产明细")).click();
        new WebDriverWait(webDriver, 1)
                .withMessage("等待资产明细界面出现超时")
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("commission")));
    }

    /**
     * 切换到团队界面
     */
    @SuppressWarnings("unused")
    private void switchToTeam() {
        webDriver.findElement(By.linkText("我的团队")).click();
        new WebDriverWait(webDriver, 1)
                .withMessage("等待我的团队界面出现超时")
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("myTeam")));
    }

    public WechatWithdrawRecordPage toWithdrawRecordPage() {
        switchToCommission();
        webDriver.findElement(By.linkText("提现记录")).click();
        return initPage(WechatWithdrawRecordPage.class);
    }

    public void clickAnyOne() {
        MemberInfo info = teamMemberStream()
                .map(MemberInfo::ofDiv)
                .max(new SpringWebTest.RandomComparator())
                .orElse(null);
        log.info("try to click:" + info);
        clickMember(info);
    }

    /**
     * 点击这个成员
     *
     * @param info
     */
    public void clickMember(MemberInfo info) {
        final WebElement targetLink = teamMemberStream()
                .filter(element -> info.equals(MemberInfo.ofDiv(element)))
                // 寻找它的上级
                .map(element -> (WebElement) ((JavascriptExecutor) webDriver).executeScript(
                        "return arguments[0].parentNode;", element))
                .peek(log::info)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到" + info));
//        targetLink
//                .click();
        log.info("clicked on " + info);
        webDriver.get(targetLink.getAttribute("href"));
//        Thread.sleep(1000L);
    }

    private Stream<WebElement> teamMemberStream() {
        // 如果找到了 J_memberList 就用J_memberList
        final String regionId;
        if (webDriver.findElements(By.id("J_memberList")).isEmpty()) {
            regionId = "J_subordinate";
        } else
            regionId = "J_memberList";
        WebDriverUtil.waitFor(webDriver, driver -> {
            if (driver.findElement(By.id(regionId)).findElements(By.className("view-team-list_items")).stream()
                    .filter(element -> !element.getAttribute("class").contains("view_team-header"))
                    .filter(element -> element.findElement(By.tagName("div")).getText().trim().length() > 0).count() != 0)
                return true;
            String text = driver.findElement(By.id(regionId))
                    .findElement(By.className("weui-loadmore__tips"))
                    .getText();
            log.info("current tip:" + text);
            return !text.contains("加载");
        }, 3);
//
//        try {
//            WebDriverUtil.waitFor(webDriver, driver -> {
//                String text = driver.findElement(By.id("J_subordinate"))
//                        .findElement(By.className("weui-loadmore__tips"))
//                        .getText();
//                return !text.contains("加载");
//            },2);
////            new WebDriverWait(webDriver, 2)
////                    .until(ExpectedConditions.invisibilityOfElementLocated(By.className("weui-loadmore__tips")));
//        }catch (TimeoutException ignored){
//        }
//
//        printThisPage();
        return webDriver.findElement(By.id(regionId)).findElements(By.className("view-team-list_items")).stream()
                .filter(element -> !element.getAttribute("class").contains("view_team-header"))
                .filter(element -> element.findElement(By.tagName("div")).getText().trim().length() > 0)
                ;
    }

    /**
     * 校验看到的成员如list所示
     *
     * @param list
     */
    public void assertTeamMembers(List<MemberInfo> list) {
        assertThat(teamMemberStream()
                .peek(System.out::println)
                .map(MemberInfo::ofDiv)
                .peek(System.out::println)
                .collect(Collectors.toSet()))
                .as("没有显示其他错误的成员")
                .containsOnlyElementsOf(list)
                .as("也没有任何遗漏")
                .containsAll(list);

    }
}
