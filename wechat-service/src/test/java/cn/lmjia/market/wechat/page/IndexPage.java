package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * 首页，目前算是 模拟页面 - 微信管理平台
 *
 * @author CJ
 */
public class IndexPage extends AbstractWechatPage {

    public IndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("模拟页面 - 微信管理平台");
    }
}
