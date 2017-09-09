package cn.lmjia.market.dealer.controller.order;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.dealer.DealerServiceTest;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
public class OrderDataControllerTest extends DealerServiceTest {

    private static final Log log = LogFactory.getLog(OrderDataControllerTest.class);
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private LocalDateConverter localDateConverter;
    private Login testLogin;

    @Before
    public void startTest() {
        testLogin = newRandomLogin();
    }

    @Override
    protected Login allRunWith() {
        return testLogin;
    }

    @Test
    public void manageableList() throws Exception {

        final Login order = testLogin;
        newRandomOrderFor(order, order);

        orderDataList(null);
        // 按业务订单号查询
        newRandomOrderFor(order, order);
        String serialId = mainOrderService.allOrders().stream()
                .filter(mainOrder -> mainOrder.getOrderBy().equals(order))
                .max(Comparator.comparing(MainOrder::getId))
                .orElse(null)
                .getSerialId();

        assertCurrentCount(builder -> builder.param("orderId", serialId), 1);

        // 按手机号码查询
        // 流程 先查询当前量,再新增，再查询
        String mobile = randomMobile();
        int mobileCurrent = currentCount(builder -> builder.param("phone", mobile));
        newRandomOrderFor(order, order);
        newRandomOrderFor(order, order, mobile);
        assertCurrentCount(builder -> builder.param("phone", mobile), mobileCurrent + 1);

        //按下单时间
        //先把之前订单全部调整到1个月之前
        //则当前数量必然为0
        //再下单 断言为1
        mainOrderService.updateOrderTime(LocalDateTime.now().minusMonths(1));
        assertCurrentCount(builder -> builder.param("orderDate", localDateConverter.print(LocalDate.now(), null)), 0);
        newRandomOrderFor(order, order);
        assertCurrentCount(builder -> builder.param("orderDate", localDateConverter.print(LocalDate.now(), null)), 1);
        //新的日期测试

        log.info("beginDate 今天只有一单");
        assertCurrentCount(builder -> builder.param("beginDate", localDateConverter.print(LocalDate.now(), null)), 1);
        log.info("beginDate 今天+1 没有");
        assertCurrentCount(builder -> builder.param("beginDate", localDateConverter.print(LocalDate.now().plusDays(1), null)), 0);
        log.info("endDate 32天前为0");
        assertCurrentCount(builder -> builder.param("endDate", localDateConverter.print(LocalDate.now().minusDays(32), null)), 0);
        //状态 略过测试
        //商品 略过测试
    }

    private ResultActions assertCurrentCount(Function<MockHttpServletRequestBuilder, MockHttpServletRequestBuilder> addParam, int count) throws Exception {
        return orderDataList(addParam, count > 0)
//                .andDo(print())
                .andExpect(jsonPath("$.recordsTotal").value(count));
    }

    private int currentCount(Function<MockHttpServletRequestBuilder, MockHttpServletRequestBuilder> addParam) throws Exception {
        MockHttpServletRequestBuilder builder = get("/orderData/manageableList");
        builder = addParam.apply(builder);
        return JsonPath.read(
                mockMvc.perform(
                        builder
                )
                        .andReturn().getResponse().getContentAsString()
                , "$.recordsTotal"
        );
    }

}