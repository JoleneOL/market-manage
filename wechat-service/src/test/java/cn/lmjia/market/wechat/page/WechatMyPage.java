package cn.lmjia.market.wechat.page;

import org.assertj.core.api.AbstractBigDecimalAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;

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

    public WechatWithdrawRecordPage toWithdrawRecordPage() {
        switchToCommission();
        webDriver.findElement(By.linkText("提现记录")).click();
        return initPage(WechatWithdrawRecordPage.class);
    }
}
