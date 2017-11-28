package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.SpringWebTest;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    public AbstractCharSequenceAssert<?, String> assertMobile() {
        return assertThat(webDriver.findElement(By.id("J_mobile")).getText());
    }

    public void changeName(String name) {
        webDriver.findElement(By.id("J_modifyName")).click();
        layerPrompt((s, webElement) -> {
            WebElement input = webElement.findElement(By.tagName("input"));
            input.clear();
            input.sendKeys(name);
            return true;
        });
    }

    public void changeMobile(String mobile) {
        webDriver.findElement(By.id("J_modifyMobile")).click();
        final By newMobileBy = By.name("newMobile");
        new WebDriverWait(webDriver, 2)
                .until(ExpectedConditions.visibilityOfElementLocated(newMobileBy));

        WebElement newMobile = webDriver.findElement(newMobileBy);

        newMobile.clear();
        newMobile.sendKeys(mobile);

        webDriver.findElement(By.id("J_confirmModifyMobile")).click();
//        layerPrompt((s, webElement) -> {
//            WebElement input = webElement.findElement(By.tagName("input"));
//            input.clear();
//            input.sendKeys(mobile);
//            return true;
//        });
    }

    /**
     * @return 引导者
     */
    public AbstractCharSequenceAssert<?, String> assertGuideName() {
        return assertThat(webDriver.findElement(By.id("J_guideName")).getText());
    }

    /**
     * @return 上级代理商
     */
    public AbstractCharSequenceAssert<?, String> assertSuperiorName() {
        return assertThat(webDriver.findElement(By.id("J_superiorName")).getText());
    }

//    /**
//     * 检查下是否有弹出框，有的话function就会被执行
//     *
//     * @param function 参数分别为弹出窗标题，整个弹出界面的div；如果返回true则表示输入，返回false就直接关闭
//     */
//    public void layerConfirm(BiFunction<String, WebElement, Boolean> function) {
//        final By locator = By.className("layui-layer-prompt");
//        layerInputAndYes(function, locator);
//    }


    public void changeGuide(Login login) {
        changeGuide(login, "修改成功");
    }

    private void changeGuide(Login login, String expected) {
        // 开启修改
        webDriver.findElement(By.id("J_modifyGuide")).click();
        // 确认同意一次性修改
//        printThisPage();
        layerDialog((s, webElement) -> true);
        new WebDriverWait(webDriver, 2).until(ExpectedConditions.visibilityOfElementLocated(By.id("J_confirmModifyGuide")));

        // 输入名字
        select2For("#guideInput", login.getLoginName(), webElement -> webElement.getText().contains(login.getLoginName()));

        // 确认修改
        webDriver.findElement(By.id("J_confirmModifyGuide")).click();

        assertLayerMessage().isEqualTo(expected);
    }

    public void changeSuperior(Login login) {
        // 开启修改
        webDriver.findElement(By.id("J_modifySuperior")).click();
        new WebDriverWait(webDriver, 2).until(ExpectedConditions.visibilityOfElementLocated(By.id("J_confirmModify")));

        // 输入名字
        select2For("#superiorInput", login.getLoginName(), webElement
                        -> {
                    return true;
                }
//            System.out.printf(login.getLoginName());
//            System.out.println(webElement.getText());
//            return webElement.getText().contains(login.getLoginName());
//                }
        );

        // 确认修改
        webDriver.findElement(By.id("J_confirmModify")).click();
        assertLayerMessage().isEqualTo("修改成功");
    }

    public void changeGuideAndFailed(Login login) {
        changeGuide(login, "用户不能互为引导者");
    }
}
