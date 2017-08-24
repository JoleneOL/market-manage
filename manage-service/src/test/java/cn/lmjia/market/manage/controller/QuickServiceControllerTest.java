package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 临时服务
 * 测试点
 * 1,下单并且支付之后 可以看到 quickDoneAble
 * 2,执行/orderData/quickDone/x 可以完成订单
 *
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
@ActiveProfiles("mysql2")
public class QuickServiceControllerTest extends ManageServiceTest {

    @Override
    protected Login allRunWith() {
        return newRandomManager();
    }

    @Test
    public void go() throws Exception {
        MainOrder order = newRandomOrderFor(randomLogin(false), randomLogin(false));

        orderDataList(builder -> builder.param("orderId", order.getSerialId()), true)
                .andExpect(jsonPath("$.data[0].quickDoneAble").value(false));

        makeOrderPay(order);

        orderDataList(builder -> builder.param("orderId", order.getSerialId()), true)
                .andExpect(jsonPath("$.data[0].quickDoneAble").value(true));

        // 执行
        mockMvc.perform(
                put("/orderData/quickDone/{0}", order.getId())
        )
                .andExpect(status().is2xxSuccessful());

        orderDataList(builder -> builder.param("orderId", order.getSerialId()), true)
                .andExpect(jsonPath("$.data[0].quickDoneAble").value(false));

        // 页面
        driver.get("http://localhost/orderManage");
        assertThat(driver.getTitle())
                .isEqualTo("用户订单");

    }

}