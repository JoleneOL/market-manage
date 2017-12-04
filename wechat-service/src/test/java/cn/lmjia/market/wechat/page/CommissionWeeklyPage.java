package cn.lmjia.market.wechat.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 佣金周报页面
 * @author lxf
 */
public class CommissionWeeklyPage extends AbstractWechatPage {

    public CommissionWeeklyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("佣金周报");
    }

    /**
     * 打印当前页
     */
    public void printPage(){
        printThisPage();
    }

    /**
     * 当前页是否渲染除了可提现佣金
     * Todo:不知道怎么用模拟微信账户访问这个网页.目前结果不符合预期
     * @param amount 可以提现的佣金钱数
     */
    public void assertAmount(BigDecimal amount){
        int qian = amount.intValue();
        assertThat(webDriver.findElement(By.tagName("h1")).getText()
                .equalsIgnoreCase(qian+""))
                .as("确保我们看到了这个可以提现的佣金")
                .isTrue();
    }

    /**
     * 传进来下单的用户名.
     * Todo:跟上面的情况一样.
     * @param name 佣金产生的订单的下单者.
     */
    public void assertCommissionDetail(String name){
        assertThat(webDriver.findElements(By.className("view-comm-list_item")).stream()
                .map(webElement -> webElement.findElement(By.className("weui-flex__item")))
                .map(WebElement::getText)
                .anyMatch(name::equalsIgnoreCase))
                .as("确保有这个名字用户的订单")
                .isTrue();
    }
}
