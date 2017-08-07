package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageStorageDeliveryPage;
import cn.lmjia.market.manage.page.ManageStoragePage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageStorageControllerTest extends ManageServiceTest {

    @Before
    public void init() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        addNewHaierDepot();
        addNewFactory();
    }

    @Test
    public void go() throws Exception {

        driver.get("http://localhost/manageStorage");
        ManageStoragePage manageStoragePage = initPage(ManageStoragePage.class);

//        manageStoragePage = initPage(ManageStoragePage.class);
        mockMvc.perform(get("/manage/storage"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ManageStorageDeliveryPage deliveryPage = manageStoragePage.clickDelivery();

        // 随机批货
        final int amount = random.nextInt(30) + 1;
        deliveryPage.submitAsAmount(amount);
        // todo 还需要加入即将入库的信息
//        deliveryPage.clickBreadcrumb();

    }

}