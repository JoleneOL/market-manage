package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageLoginPage;
import org.junit.Test;

/**
 * @author CJ
 */
public class ManageLoginControllerTest extends ManageServiceTest {

    @Test
    public void go() {
        updateAllRunWith(newRandomManager(ManageLevel.root));

        ManageLoginPage.of(this, driver);
//        ManageLoginPage page = ManageLoginPage.of(this, driver);
//        page.printThisPage();
    }

}