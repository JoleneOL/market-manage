package cn.lmjia.market.wechat.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.wechat.page.AbstractWechatPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HelpIndexPage extends AbstractWechatPage {

    @FindBy(xpath="a[@class='weui-cell weui-cell_access']")
    private WebElement helpDetail;

    @FindBy(id = "J_searchInput")
    private WebElement searchInput;

    public HelpIndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("帮助中心");
    }

    /**
     * 校验某个常见问题
     * @param commonProblem
     */
    public void validataHelp(CommonProblem commonProblem){

    }
}
