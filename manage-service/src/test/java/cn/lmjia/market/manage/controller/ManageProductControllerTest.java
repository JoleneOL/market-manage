package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageProductPage;
import cn.lmjia.market.manage.page.ProductCreatePage;
import cn.lmjia.market.manage.page.ProductDetailPage;
import cn.lmjia.market.manage.page.ProductEditPage;
import cn.lmjia.market.manage.page.ProductOperatePage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试流程，浏览界面
 * 点击添加货品
 * 输入部分资料
 * 完成之后回到主页
 * 点击推送，应该是报错
 * 进入编辑，
 * 发现部分资料已被冻结
 * 填写所有资料
 * 回到首页推送，成功
 *
 * @author CJ
 */
public class ManageProductControllerTest extends ManageServiceTest {

    @Override
    protected Login allRunWith() {
        return newRandomManager(ManageLevel.root);
    }

    @Test
    public void go() throws InterruptedException {
        driver.get("http://localhost/manageProduct");
        ManageProductPage manageProductPage = initPage(ManageProductPage.class);

        // 禁用和激活
        // 禁用掉第一个，并且获取它的code
        String code = manageProductPage.clickDisable();
        // 然后重新激活
        manageProductPage.clickEnable(code);
        // 第一个肯定是刚才那个被激活的
        assertThat(manageProductPage.clickDisable())
                .isEqualTo(code);

        /////////////////

        // 点击新增
        ProductCreatePage createPage = manageProductPage.clickNew();
        //来回点击
        createPage.clickBreadcrumb();
        manageProductPage = initPage(ManageProductPage.class);
        createPage = manageProductPage.clickNew();
        manageProductPage.printHtml();

        // 提交一份并不算完整的数据
        manageProductPage = createPage.submitWithout(randomArray(ProductOperatePage.FieldForOnceLock, 3));

        // 排序原则 第一个肯定是最新的
        // 推送 这个时候肯定是不行的
        manageProductPage.clickPushHaierForFirstRow();
        manageProductPage.clickLayerButton(0);
        manageProductPage.assertInfo().isNotNull().contains("信息不完整");

        ProductDetailPage detailPage = manageProductPage.clickViewForFirstRow();
        // 先到详情
        detailPage.clickBreadcrumb();
        manageProductPage = initPage(ManageProductPage.class);

        detailPage = manageProductPage.clickViewForFirstRow();
        ProductEditPage editPage = detailPage.clickEdit();
        editPage.clickBreadcrumb();

        manageProductPage = initPage(ManageProductPage.class);

        detailPage = manageProductPage.clickViewForFirstRow();
        editPage = detailPage.clickEdit();
        // 重新编辑，让它变得完整
        manageProductPage = editPage.submitWithout();

        manageProductPage.clickPushHaierForFirstRow();
        manageProductPage.clickLayerButton(0);
        // 可能会失败的
        manageProductPage.assertInfo().isNotNull().contains("成功");

    }

}