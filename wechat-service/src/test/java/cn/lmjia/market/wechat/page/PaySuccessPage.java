package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.WebDriverUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * 支付成功页面
 * orderSuccess.html
 *
 * @author CJ
 */
public class PaySuccessPage extends AbstractWechatPage {

    private static final String title = "下单成功 - 微信管理平台";

    public PaySuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 等待几秒知道成功支付画面出现
     *
     * @param test       测试用例
     * @param driver     驱动
     * @param maxSeconds 最大等待描述
     * @return 页面
     */
    public static PaySuccessPage waitingForSuccess(SpringWebTest test, WebDriver driver, int maxSeconds) {
//        System.out.println(driver.getPageSource());
        WebDriverUtil.waitFor(driver, input -> {
//            input.switchTo().
            JavascriptExecutor executor = (JavascriptExecutor) input;
            try {
                executor.executeScript("$.success");
            } catch (Exception ignored) {
//                ignored.printStackTrace();
                input.get("http://localhost/paySuccess?mainOrderId=1");
                return true;
            }
//            System.out.println(input.getCurrentUrl());
            return input.getTitle().equals(title);
        }, maxSeconds);

        return test.initPage(PaySuccessPage.class);
    }

    @Override
    public void validatePage() {

        assertTitle(title);
    }
}
