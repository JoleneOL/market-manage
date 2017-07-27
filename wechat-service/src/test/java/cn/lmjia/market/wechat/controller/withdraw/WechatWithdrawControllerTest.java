package cn.lmjia.market.wechat.controller.withdraw;


import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.WechatTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ContextConfiguration(classes = SecurityConfig.class)
public class WechatWithdrawControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatWithdrawControllerTest.class);

    @Test
    public void doWithdraw() throws Exception {
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        String withdrawUri = mockMvc.perform(wechatPost("/wechatWithdraw")
                .param("payee", "oneal")
                .param("account", "6217001480003532428")
                .param("bank", "建设银行")
                .param("mobile", "15267286525")
                .param("withdrawMoney", "500.00")
                .param("logisticsNumber", "710389211847")
                .param("logisticsCompany", "圆通物流")
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + withdrawUri);

    }
}
