package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 代理商详情后台页面
 * _agentDetail.html
 *
 * @author CJ
 */
public class ManageAgentDetailPage extends AbstractContentPage {

    public ManageAgentDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static ManageAgentDetailPage of(AgentLevel agentLevel, SpringWebTest instance, WebDriver driver) {
        driver.get("http://localhost/manageAgentDetail?id=" + agentLevel.getId());
        return instance.initPage(ManageAgentDetailPage.class);
    }

    @Override
    public void validatePage() {

    }

    /**
     * @return 断言名字
     */
    public AbstractCharSequenceAssert<?, String> assertName() {
        return assertThat(webDriver.findElement(By.id("J_loginName")).getText());
    }

    public void changeName(String name) {

    }
}
