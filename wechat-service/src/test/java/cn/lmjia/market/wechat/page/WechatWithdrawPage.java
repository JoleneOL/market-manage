package cn.lmjia.market.wechat.page;


import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 我要提现
 * withdraw.html
 */
public class WechatWithdrawPage extends AbstractWechatPage {

    @FindBy(name = "payee")
    private WebElement payee;
    @FindBy(id = "J_Bank")
    private WebElement account;
    @FindBy(name = "bank")
    private WebElement bank;
    @FindBy(name = "mobile")
    private WebElement mobile;
    @FindBy(name = "withdraw")
    private WebElement withdraw;

    // 发票
    @FindBy(id = "J_haveInvoice")
    private WebElement haveInvoice;
    @FindBy(id = "J_noInvoice")
    private WebElement noInvoice;
    // 物流
    @FindBy(name = "logisticsCode")
    private WebElement logisticsCode;
    @FindBy(name = "logisticsCompany")
    private WebElement logisticsCompany;

    @FindBy(css = "button[type=submit]")
    private WebElement submit;

    public WechatWithdrawPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我要提现");
    }

    /**
     * 随机填入数据，然后不提供发票并且提现特定金额
     *
     * @param amount 特定金额
     */
    public void randomRequestWithoutInvoice(String amount) {
        randomRequestWithAmount(amount);
        noInvoice.click();
        submit.click();
    }

    private void randomRequestWithAmount(String amount) {
        payee.clear();
        payee.sendKeys(RandomStringUtils.randomAlphabetic(8));
        account.clear();
        account.sendKeys(RandomStringUtils.randomNumeric(18));
        bank.clear();
        bank.sendKeys(RandomStringUtils.randomAlphabetic(8));
        mobile.clear();
        mobile.sendKeys("13" + RandomStringUtils.randomNumeric(9));
        withdraw.clear();
        withdraw.sendKeys(amount);
    }

    /**
     * 随机填入数据，然后提供发票并且提现特定金额
     *
     * @param amount 特定金额
     */
    public void randomRequestWithInvoice(String amount) {
        randomRequestWithAmount(amount);
        haveInvoice.click();
        logisticsCode.clear();
        logisticsCode.sendKeys(RandomStringUtils.randomAlphabetic(10));
        logisticsCompany.clear();
        logisticsCompany.sendKeys(RandomStringUtils.randomAlphabetic(10));
        submit.click();
    }
}
