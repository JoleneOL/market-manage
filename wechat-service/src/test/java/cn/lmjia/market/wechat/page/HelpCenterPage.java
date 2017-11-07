package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

/**
 * 帮助中心的逻辑页面
 * 它的模版是/helpcenter/index.html
 */
public class HelpCenterPage extends AbstractWechatPage {
    public HelpCenterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("帮助");
    }

    /**
     *
     * @param title 確認頁面渲染出了這個標題的幫助
     */
    public void assertHasTopic(String title) {
        // TODO 不知怎麽實現
    }
}
