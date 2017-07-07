package cn.lmjia.market.wechat.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 分享页面
 * shareQC.html
 *
 * @author CJ
 */
public class WechatSharePage extends AbstractWechatPage {
    public WechatSharePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("分享二维码");
    }

    public String getShareUrl() {
        return webDriver.findElement(By.tagName("body")).getAttribute("data-url");
    }
}
