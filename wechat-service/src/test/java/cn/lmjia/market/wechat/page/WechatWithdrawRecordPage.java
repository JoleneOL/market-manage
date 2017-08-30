package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * 提现记录
 * withdrawRecord.html
 *
 * @author CJ
 */
public class WechatWithdrawRecordPage extends AbstractWechatPage {

    public WechatWithdrawRecordPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("提现记录");
    }
}
