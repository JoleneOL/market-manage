package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageStoragePage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author CJ
 */
public class ManageStorageControllerTest extends ManageServiceTest {

    @Before
    public void init() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        addNewHaierDepot();
    }

    @Test
    public void go() throws Exception {

        driver.get("http://localhost/manageStorage");
        ManageStoragePage manageStoragePage = initPage(ManageStoragePage.class);

        manageStoragePage.clickDelivery();
        driver.navigate().back();

        manageStoragePage = initPage(ManageStoragePage.class);

    }

}