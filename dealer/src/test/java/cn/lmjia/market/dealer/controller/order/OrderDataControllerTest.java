package cn.lmjia.market.dealer.controller.order;

import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;

/**
 * @author CJ
 */
public class OrderDataControllerTest extends DealerServiceTest {
    @Test
    public void manageableList() throws Exception {

        mockMvc.perform(
                get("/orderData/manageableList")
        )
//                        .andDo(print())
                .andExpect(similarSelect2("classpath:/dealer-view/mock/orderData.json"));
    }

}