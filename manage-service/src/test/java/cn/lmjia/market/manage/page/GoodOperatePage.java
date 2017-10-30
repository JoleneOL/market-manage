package cn.lmjia.market.manage.page;

import cn.lmjia.market.core.pages.AbstractContentPage;
import me.jiangcai.lib.test.page.WebDriverUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.Iterator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商品可编辑页面；
 * 可能是新建或者编辑
 * _goodOperate.html
 *
 * @author CJ
 */
public abstract class GoodOperatePage extends AbstractContentPage {

    @FindBy(name = "product")
    private WebElement product;
//    @FindBy(name = "channel")
//    private WebElement channel;

    public GoodOperatePage(WebDriver webDriver) {
        super(webDriver);
    }


    /**
     * @return 是否新建啊
     */
    protected abstract boolean isCreateNew();

    /**
     * 提交 并且设置渠道为空
     *
     * @return
     */
    public ManageGoodPage submitWithoutChannel() {
        if (product.isEnabled()) {
            inputSelect(webDriver.findElement(By.tagName("form")), "product", s -> true);
        }
        inputSelect(webDriver.findElement(By.tagName("form")), "channel", "无");
        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageGoodPage.class);
    }

    public ManageGoodPage submitWithChannel() {
        //只有添加商品时选择货品才有效
        if (webDriver.getTitle().contains("新增商品") && product.isEnabled()) {
            inputSelect(webDriver.findElement(By.tagName("form")), "product", s -> true);
        }
        inputSelect(webDriver.findElement(By.tagName("form")), "channel", x -> !"无".equals(x));
        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageGoodPage.class);
    }

    public ManageGoodPage submitWithTag() throws InterruptedException {
        if (product.isEnabled()) {
            inputSelect(webDriver.findElement(By.tagName("form")), "product", s -> true);
        }
        //先随便添加一个标签
        String randomTag = RandomStringUtils.randomAlphabetic(5) + "标签";
        assertThat(addTag(randomTag)).isTrue();
        //已经存在的添加失败
        assertThat(addTag(randomTag)).isFalse();
        initMultiSelect(webDriver.findElement(By.tagName("form")), "tag", x -> randomTag.equals(x));
        webDriver.findElement(By.cssSelector("[type=submit]")).click();
        return initPage(ManageGoodPage.class);
    }

    private boolean addTag(String name) throws InterruptedException {
        //先看看原来 option 有几个
        int beforeOptionNum = webDriver.findElement(By.id("J_selectTag")).findElements(By.tagName("option")).size();
        webDriver.findElement(By.id("J_addTag")).click();
        WebElement addTagName = webDriver.findElement(By.className("layui-layer-input"));
        addTagName.clear();
        if (name != null) {
            addTagName.sendKeys(name);
        }
        webDriver.findElement(By.className("layui-layer-btn0")).click();
        Thread.sleep(3000);
        //看看现在 option 有几个
        int afterOptionNum = webDriver.findElement(By.id("J_selectTag")).findElements(By.tagName("option")).size();
        if (afterOptionNum > beforeOptionNum) {
            return true;
        }
        return false;
    }

    private void initMultiSelect(WebElement formElement, String inputName, Function<String, Boolean> useIt) throws InterruptedException {
        WebElement select = formElement.findElement(By.name(inputName));
        WebElement div = select.findElement(By.xpath("following-sibling::div[1]"));
        //先点一下
        Actions action = new Actions(webDriver);
        /*调用Action对象的clickAndHold方法，在ID属性值为div1的页面元素上方单击鼠标不释放*/
        action.clickAndHold(div).perform();
        Thread.sleep(10);
        action.release(div).perform();
        Thread.sleep(10);

        Iterator container1 = div.findElement(By.className("chosen-results")).findElements(By.tagName("li")).iterator();

        WebElement element;
        do {
            if (!container1.hasNext()) {
                throw new IllegalStateException("找不到符合要求的Label");
            }

            element = (WebElement) container1.next();
        } while (!useIt.apply(element.getText()));

        action.clickAndHold(element).perform();
        Thread.sleep(10);
        action.release(element).perform();
        Thread.sleep(10);
        printThisPage();
    }
}
