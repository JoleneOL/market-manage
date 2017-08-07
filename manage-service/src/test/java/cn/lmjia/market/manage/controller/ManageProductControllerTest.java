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
    public void go() {
        driver.get("http://localhost/manageProduct");
        ManageProductPage manageProductPage = initPage(ManageProductPage.class);

        ProductCreatePage createPage = manageProductPage.clickNew();
        //来回点击
        createPage.clickBreadcrumb();
        manageProductPage = initPage(ManageProductPage.class);
        createPage = manageProductPage.clickNew();

        manageProductPage = createPage.submitWithout(randomArray(ProductOperatePage.FieldForOnceLock, 3));

        // 排序原则 第一个肯定是最新的
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
        manageProductPage = editPage.submitWithout();

        manageProductPage.clickPushHaierForFirstRow();
        // 可能会失败的
        manageProductPage.assertInfo().isNotNull().contains("成功");

        // 禁用和激活
        String code = manageProductPage.clickDisable();
        manageProductPage.clickLayerButton(0);
        manageProductPage.clickEnable(code);
        assertThat(manageProductPage.clickDisable())
                .isEqualTo(code);
    }

}