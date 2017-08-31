package cn.lmjia.market.wechat.page;

import cn.lmjia.market.wechat.model.MemberInfo;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.openqa.selenium.By;
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

    /**
     * 点击这个成员
     *
     * @param info
     */
    public void clickMember(MemberInfo info) {
        teamMemberStream()
                .filter(element -> info.equals(MemberInfo.ofDiv(element)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到" + info))
                .click();
    }

    private Stream<WebElement> teamMemberStream() {
        return webDriver.findElements(By.className("view-team-list_items")).stream()
                .filter(element -> !element.getAttribute("class").contains("view_team-header"));
    }

    /**
     * 校验看到的成员如list所示
     *
     * @param list
     */
    public void assertTeamMembers(List<MemberInfo> list) {
        assertThat(teamMemberStream()
                .map(MemberInfo::ofDiv)
                .collect(Collectors.toSet()))
                .as("只包含这些")
                .containsExactlyElementsOf(list);

    }
}
