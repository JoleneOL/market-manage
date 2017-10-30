package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageAgentGoodAdvancePaymentPage;
import org.junit.Test;

/**
 * @author CJ
 */
public class ManageAgentGoodAdvancePaymentControllerTest extends ManageServiceTest {

    @Test
    public void go() {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageAgentGoodAdvancePaymentPage.of(this, driver);
        // 添加一个
    }

}