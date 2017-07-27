package cn.lmjia.market.wechat.page;


import org.openqa.selenium.WebDriver;

/**
 * 提现页面
 * withdraw.html
 */
public class WechatWithdrawPage extends AbstractWechatPage{

    public WechatWithdrawPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("提现页面");
    }
}
