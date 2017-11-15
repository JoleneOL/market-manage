package cn.lmjia.market.wechat.page;


import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 我要提现
 * withdraw.html
 */
public class WechatWithdrawPage extends AbstractWechatPage {

    @FindBy(name = "payee")
    private WebElement payee;
    @FindBy(id = "J_Bank")
    private WebElement account;
    @FindBy(css = "input[name=account]")
    private WebElement realAccount;
    @FindBy(name = "bank")
    private WebElement bank;
    @FindBy(name = "bankCity")
    private WebElement bankCity;
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
    public void randomRequestWithoutInvoice(String amount) throws InterruptedException {
        randomRequestWithAmount(amount);
        noInvoice.click();
        submit.click();
    }

    private void randomRequestWithAmount(String amount) throws InterruptedException {
        payee.clear();
        payee.sendKeys(RandomStringUtils.randomAlphabetic(8));
        account.clear();
        final char[] accountChars = RandomStringUtils.randomNumeric(18).toCharArray();
        for (char accountChar : accountChars) {
            account.sendKeys(new String(new char[]{accountChar}));
            if (realAccount.getAttribute("value").length() >= amount.length())
                break;
            Thread.sleep(100L);
        }
//        account.sendKeys(RandomStringUtils.randomNumeric(18));
//        Thread.sleep(500);
        bank.clear();
        bank.sendKeys(RandomStringUtils.randomAlphabetic(8));
        bankCity.clear();
        //开户行城市,随便写来个杭州.
        bankCity.sendKeys("杭州市");
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
    public void randomRequestWithInvoice(String amount) throws InterruptedException {
        randomRequestWithAmount(amount);
        haveInvoice.click();
        logisticsCode.clear();
        logisticsCode.sendKeys(RandomStringUtils.randomAlphabetic(10));
        logisticsCompany.clear();
        logisticsCompany.sendKeys(RandomStringUtils.randomAlphabetic(10));
        submit.click();
    }

    /**
     * 同意规则
     */
    public void agreeRules() {
        webDriver.findElement(By.id("rules")).click();
        new WebDriverWait(webDriver, 2)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("J_agree_button")));
        webDriver.findElement(By.id("J_agree_button")).click();
    }
}
