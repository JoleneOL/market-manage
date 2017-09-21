package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.GoodCreatePage;
import cn.lmjia.market.manage.page.GoodEditPage;
import cn.lmjia.market.manage.page.ManageGoodPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试流程，浏览界面
 * 点击添加商品
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
public class ManageGoodControllerTest extends ManageServiceTest {

    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Override
    protected Login allRunWith() {
        return newRandomManager(ManageLevel.root);
    }

    @Test
    public void go() throws Exception {
        driver.get("http://localhost/manageGood");
        ManageGoodPage manageGoodPage = initPage(ManageGoodPage.class);

        // 禁用和激活
        // 禁用掉第一个，并且获取它的code
        String code = manageGoodPage.clickDisable();
        // 然后重新激活
        manageGoodPage.clickEnable(code);
        // 第一个肯定是刚才那个被激活的
        assertThat(manageGoodPage.clickDisable())
                .isEqualTo(code);
        manageGoodPage.clickEnable(code);

        /////////////////

        // 点击新增
        GoodCreatePage createPage = manageGoodPage.clickNew();
        //来回点击
        createPage.clickBreadcrumb();
        manageGoodPage = initPage(ManageGoodPage.class);
        createPage = manageGoodPage.clickNew();

        // 提交一份数据
        manageGoodPage = createPage.submitWithoutChannel();
        MainGood createdGood = mainGoodRepository.findByEnableTrue().stream()
                .max(Comparator.comparing(MainGood::getId)).orElse(null);

        assertThat(createdGood.getChannel())
                .isNull();
        assertThat(createdGood.getTags())
                .isEmpty();

        GoodEditPage editPage = manageGoodPage.clickEditForFirstRow();
        editPage.clickBreadcrumb();

        manageGoodPage = initPage(ManageGoodPage.class);

        editPage = manageGoodPage.clickEditForFirstRow();
        // 重新编辑，让它变得完整
        editPage.submitWithChannel();
//        assertThat(mainGoodRepository.getOne(createdGood.getId()).getChannel())
//                .isNotNull();

        // TODO: 2017-09-21  
        //先加一个标签
        /*addNewTag();
        editPage = manageGoodPage.clickEditForFirstRow();
        editPage.clickBreadcrumb();

        manageGoodPage = initPage(ManageGoodPage.class);

        editPage = manageGoodPage.clickEditForFirstRow();
        // 添加商品标签
        editPage.submitWithTag();
        assertThat(mainGoodRepository.getOne(createdGood.getId()).getTags())
                .isNotEmpty();*/
    }

}