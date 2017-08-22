package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageWithdrawPage;
import org.junit.Test;

/**
 * @author CJ
 */
public class ManageWithdrawControllerTest extends ManageServiceTest {


    @Test
    public void go() {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageWithdrawPage page = ManageWithdrawPage.of(this, driver);

        // 可以检查是否存在发票

    }

}