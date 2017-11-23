package cn.lmjia.market.wechat.page;

import me.jiangcai.lib.test.SpringWebTest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.NumberUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 下单页面
 * orderPlace.html
 *
 * @author CJ
 */
public class WechatOrderPage extends AbstractWechatPage {

    //    @FindBy(id = "J_orderTotal")
//    private WebElement totalPrice;
    @FindBy(id = "J_addGoods")
    private WebElement buttonToAddGoods;
    @FindBy(id = "J_goodsList")
    private WebElement goodListRegion;
    @FindBy(name = "name")
    private WebElement name;
    @FindBy(name = "mobile")
    private WebElement mobile;

    public WechatOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static void main(String[] args) {
        // 弹出框然后让用户选择页面地址 测试类 测试方法
        if (Desktop.isDesktopSupported()) {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(new JLabel("页面地址"));
            JTextField urlField = new JTextField(60);
            panel.add(urlField);
            panel.add(new JLabel("页类全限定名"));
            JTextField classField = new JTextField(30);
            panel.add(classField);
            panel.add(new JLabel("测试代码(javascript, this==instance)"));
            JTextArea area = new JTextArea(3, 70);
            panel.add(area);
//            JOptionPane.showInternalConfirmDialog()
            JOptionPane.showConfirmDialog(null, panel, "请输入需要测试的页面以及相关方法"
                    , JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
//            JOptionPane.showMessageDialog(null, null, "请输入需要测试的页面以及相关方法"
//                    , JOptionPane.QUESTION_MESSAGE);
        }
    }

    @Override
    public void validatePage() {
        assertTitle("我的下单");
    }

    /**
     * 所有商品价格都为price
     *
     * @param priceInput 特定价格
     */
    public void allPriced(BigDecimal priceInput) {
//        BigDecimal price = priceInput.setScale(0, BigDecimal.ROUND_HALF_UP);
////        final By goodsAmount = By.id("J_goodsAmount");
////        final WebElement amount = webDriver.findElement(goodsAmount);
////        if (amount.getAttribute("readonly")!=null) {
////            // 如果只读，那么请等待它有值
////            WebDriverUtil.waitFor(webDriver, driver
////                            -> !StringUtils.isEmpty(driver.findElement(goodsAmount).getAttribute("value"))
////                    , 2);
////        }
////        System.out.println(amount.getAttribute("value"));
////        if (!"1".equals(amount.getAttribute("value")))
////            inputText(webDriver.findElement(By.id("J_form")), "amount", "1");
//        WebElement select = webDriver.findElement(By.id("J_goodsType"));
//
//        List<WebElement> options = select.findElements(By.tagName("option"));
//        for (WebElement option : options) {
//            if (option.isEnabled()) {
//                option.click();
//                // 包含该取整值
//                assertThat(totalPrice.getText())
//                        .startsWith(price.toString());
//            }
//        }
    }

    /**
     * 提交随机订单请求
     *
     * @param goodChooser   可选的商品选择；输入参数为商品名称；默认为都选择
     * @param amountChooser 可选的数量选择；输入参数为商品名称:限购数量;默认为选择1-1/4。如果界面没有提供限购数量则购买1-9个
     */
    public final void submitRandomOrder(Function<String, Boolean> goodChooser
            , Function<NameValuePair, Integer> amountChooser) {
        // 如果选择商品窗口没打开 则打开它
        if (!goodListRegion.isDisplayed()) {
            buttonToAddGoods.click();
            new WebDriverWait(webDriver, 2)
                    .until(ExpectedConditions.visibilityOf(goodListRegion));
        }

        goodListRegion.findElements(By.className("weui-media-box__bd"))
                .forEach(good -> {
                    // 第一个h4为商品名称，第二个为价格
                    String name = good.findElement(By.tagName("h4")).getText();
                    boolean choose;
                    if (goodChooser == null)
                        choose = true;
                    else
                        choose = goodChooser.apply(name);
                    if (choose) {
                        // 确定购买
                        // p.text-error 为限购信息的说明
                        //
                        int amount;
                        try {
                            WebElement limitInfo = good.findElement(By.cssSelector("p.text-error"));
                            String limitText = limitInfo.getText();
                            Matcher matcher = Pattern.compile("限购(\\d+).*").matcher(limitText);
                            assertThat(matcher.matches())
                                    .as("限购信息 跟 限购xx单位  的格式不一致:" + limitText)
                                    .isTrue();
                            int limitAmount = NumberUtils.parseNumber(matcher.group(1), Integer.class);
                            if (amountChooser == null) {
                                int targetMax = limitAmount / 4;
                                int targetMin = 1;
                                if (targetMax > targetMin)
                                    amount = new Random().nextInt(targetMax - targetMin) + targetMin;
                                else
                                    amount = Math.min(targetMin, limitAmount);
                            } else
                                amount = amountChooser.apply(new NameValuePair() {
                                    @Override
                                    public String getName() {
                                        return name;
                                    }

                                    @Override
                                    public String getValue() {
                                        return String.valueOf(limitAmount);
                                    }
                                });
                        } catch (NoSuchElementException ignore) {
                            amount = 1 + new Random().nextInt(9);
                        }

                        // 数量已确定
                        if (amount > 0) {
                            WebElement input = good.findElement(By.cssSelector("input[type=number]"));
                            input.clear();
                            input.sendKeys(String.valueOf(amount));
                        }
                    }
                });

        // 点击完成关闭商品选择
        webDriver.findElement(By.id("J_goodsOK")).click();
//        new WebDriverWait(webDriver, 2)
//                .until(ExpectedConditions.invisibilityOf(goodListRegion));

        // 其他信息
        name.clear();
        name.sendKeys("W客户" + RandomStringUtils.randomAlphabetic(6));

//        webDriver.findElements(By.name("gender")).stream()
//                .sorted(new SpringWebTest.RandomComparator())
//                .findFirst().orElse(null);

        randomAddress(webDriver.findElement(By.id("J_form")), "address", "fullAddress");

        // 手机号码
        mobile.clear();
        mobile.sendKeys(SpringWebTest.randomAllMobile());

        typeOtherInformation();

        webDriver.findElement(By.id("J_submitBtn")).click();
    }

    /**
     * 填写其他订单信息
     */
    protected void typeOtherInformation() {

    }
}
