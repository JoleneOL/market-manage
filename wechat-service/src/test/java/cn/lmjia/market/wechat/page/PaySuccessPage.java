package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.WebDriverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 支付成功页面
 * orderSuccess.html
 *
 * @author CJ
 */
public class PaySuccessPage extends AbstractWechatPage {

    private static final String title = "下单成功";
    private static final Log log = LogFactory.getLog(PaySuccessPage.class);

    public PaySuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 等待几秒知道成功支付画面出现
     *
     * @param test       测试用例
     * @param driver     驱动
     * @param maxSeconds 最大等待描述
     * @param failedUrl  如果driver失败了则直接调用该url
     * @return 页面
     */
    public static PaySuccessPage waitingForSuccess(SpringWebTest test, WebDriver driver, int maxSeconds, String failedUrl) {
//        System.out.println(driver.getPageSource());
        WebDriverUtil.waitFor(driver, input -> {
//            input.switchTo().
//            log.debug(input.getPageSource());
            if (isSuccessTitle(input))
                return true;
            log.debug(input.getTitle());
            JavascriptExecutor executor = (JavascriptExecutor) input;
            try {
                executor.executeScript("$.success");
            } catch (Exception ignored) {
//                ignored.printStackTrace();
                input.get(failedUrl);
                return true;
            }
            return isSuccessTitle(input);
        }, maxSeconds);

        return test.initPage(PaySuccessPage.class);
    }

    private static boolean isSuccessTitle(WebDriver input) {
        return input.getTitle().equals(title) || input.getTitle().equals("申请成功");
    }

    @Override
    public void validatePage() {
        assertThat(isSuccessTitle(webDriver))
                .as("标题需要符合")
                .isTrue();
    }
}
