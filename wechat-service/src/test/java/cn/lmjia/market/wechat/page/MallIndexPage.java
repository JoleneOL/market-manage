package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * Created by helloztt on 2017-09-22.
 */

public class MallIndexPage extends AbstractWechatPage {
    public MallIndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("商城");
    }
}
