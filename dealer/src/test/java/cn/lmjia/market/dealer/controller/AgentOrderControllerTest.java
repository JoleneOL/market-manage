package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
@Ignore // 暂时忽略该平台
public class AgentOrderControllerTest extends DealerServiceTest {

    @Override
    protected Login allRunWith() {
        return randomLogin(false);
    }

    @Test
    public void makeOrder() throws Exception {
        MockHttpServletRequestBuilder builder = post("/agentOrder");
        orderRequestBuilder(builder, randomOrderRequest());

        String targetUri = mockMvc.perform(
                builder
        )
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();

        driver.get("http://localhost" + targetUri);
        // 最终看到支付成功页面！
        new WebDriverWait(driver, 4)
                .until(ExpectedConditions.titleIs("下单成功 - 代理商后台管理"));
    }

}