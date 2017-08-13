package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageStorageDeliveryPage;
import cn.lmjia.market.manage.page.ManageStoragePage;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

/**
 * @author CJ
 */
//@ActiveProfiles("mysql")
public class ManageStorageControllerTest extends ManageServiceTest {

    @Before
    public void init() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        addNewHaierDepot();
        addNewFactory();
    }

    @Test
    public void go() throws Exception {
        driver.get("http://localhost/manage");
//        System.out.println(driver.getPageSource());

        driver.get("http://localhost/manageStorage");
        ManageStoragePage manageStoragePage = initPage(ManageStoragePage.class);

//        manageStoragePage = initPage(ManageStoragePage.class);
        mockMvc.perform(get("/manage/storage"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ManageStorageDeliveryPage deliveryPage = manageStoragePage.clickDelivery();

        // 随机批货
        final int amount = random.nextInt(30) + 1;
        deliveryPage.submitAsAmount(amount);


        // productCode
        // 每一个产品 都试一下 肯定只有一个产品
        checkShiftDetail(mockMvc.perform(get("/manage/factoryOut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1)));

        // 看看详情呗
        // /manageShiftDetail

        // todo 还需要加入即将入库的信息
//        deliveryPage.clickBreadcrumb();

    }

    private void checkShiftDetail(ResultActions resultActions) throws UnsupportedEncodingException {
        List<Map<String, Object>> list = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.data");
        list.forEach(data -> {
            driver.get("http://localhost/manageShiftDetail?id=" + data.get("id"));
            System.out.println(driver.getPageSource());
        });

    }

}