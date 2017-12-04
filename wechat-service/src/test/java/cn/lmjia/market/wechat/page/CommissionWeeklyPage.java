package cn.lmjia.market.wechat.page;

import org.openqa.selenium.WebDriver;

public class CommissionWeeklyPage extends AbstractWechatPage {

    public CommissionWeeklyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("佣金周报");
    }

    public void printPage(){
        printThisPage();
    }
}
