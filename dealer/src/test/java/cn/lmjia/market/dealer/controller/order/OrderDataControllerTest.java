package cn.lmjia.market.dealer.controller.order;

import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.dealer.DealerServiceTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author CJ
 */
public class OrderDataControllerTest extends DealerServiceTest {

    @Autowired
    private MainOrderService mainOrderService;

    @Test
    public void manageableList() throws Exception {

        newRandomOrderFor(randomLogin(false), randomLogin(true));

        mockMvc.perform(
                get("/orderData/manageableList")
        )
                .andDo(print())
                .andExpect(similarJQueryDataTable("classpath:/dealer-view/mock/orderData.json"));
        // 按业务订单号查询
        newRandomOrderFor(randomLogin(false), randomLogin(true));
        String serialId = mainOrderService.allOrders().stream()
                .max(new RandomComparator())
                .orElse(null)
                .getSerialId();
        mockMvc.perform(
                get("/orderData/manageableList")
                        .param("orderId", serialId)
        )
                .andExpect(similarJQueryDataTable("classpath:/dealer-view/mock/orderData.json"))
                .andExpect(jsonPath("$.data.length()").value(1));
        // 按手机号码查询
        // 流程 先查询当前量,再新增，再查询
        String mobile = randomMobile();
        int mobileCurrent = currentCount(builder -> builder.param("phone", mobile));
        newRandomOrderFor(randomLogin(false), randomLogin(true));
        newRandomOrderFor(randomLogin(false), randomLogin(true), mobile);
        assertCurrentCount(builder -> builder.param("phone", mobile), mobileCurrent + 1);

    }

    private void assertCurrentCount(Function<MockHttpServletRequestBuilder, MockHttpServletRequestBuilder> addParam, int count) throws Exception {
        MockHttpServletRequestBuilder builder = get("/orderData/manageableList");
        builder = addParam.apply(builder);
        mockMvc.perform(
                builder
        ).andExpect(jsonPath("$.data.length()").value(count));
    }

    private int currentCount(Function<MockHttpServletRequestBuilder, MockHttpServletRequestBuilder> addParam) throws Exception {
        MockHttpServletRequestBuilder builder = get("/orderData/manageableList");
        builder = addParam.apply(builder);
        return JsonPath.read(
                mockMvc.perform(
                        builder
                )
                        .andReturn().getResponse().getContentAsString()
                , "$.data.length()"
        );
    }

}