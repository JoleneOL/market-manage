package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
public abstract class AbstractWechatMainOrderControllerTest extends WechatTestBase {

    private void doOrder() throws Exception {
        mockMvc.perform(wechatGet(SystemService.wechatOrderURi))
//                    .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("wechat@orderPlace.html"));
        mockMvc.perform(wechatGet(SystemService.wechatOrderURiHB))
//                    .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("wechat@orderPlace.html"));
        // 这波则是开干了
        //
        MockHttpServletRequestBuilder requestBuilder =
                orderRequestBuilder(wechatPost("/wechatOrder"), randomOrderRequest());

        String result = mockMvc.perform(
                requestBuilder
        )
                .andExpect(status().isOk())
                .andDo(print())
//                .andExpect(jsonPath("$.resultCode").value(200))
//                .andExpect(jsonPath("$.data.id").exists())
//                .andExpect(jsonPath("$.data.installmentHuabai").exists())
                .andReturn().getResponse().getContentAsString();
        JSONObject data = JSONObject.parseObject(result).getJSONObject("data");
        Long orderPKId = data.getLong("id");
        String channelId = data.getString("channelId")
                ,idNumber = data.getString("idNumber")
                ,authorising = data.getString("authorising");
        Boolean installmentHuabai = data.getBoolean("installmentHuabai");
        StringBuilder orderPayURL = new StringBuilder("http://localhost").append(SystemService.wechatPayOrderURi)
                .append("?orderPKId=").append(orderPKId)
                .append("&installmentHuabai=").append(installmentHuabai);
        if(channelId != null){
            orderPayURL = orderPayURL
                    .append("&channelId=").append(channelId)
                    .append("&idNumber=").append(idNumber)
                    .append("&authorising=").append(authorising);
        }
        // 使用 driver 打开!
        driver.get(orderPayURL.toString());
//                    mockMvc.perform(wechatGet("/paySuccess?mainOrderId=1"))
//                            .andDo(print());
        PaySuccessPage.waitingForSuccess(this, driver, 3, "http://localhost/wechatPaySuccess?mainOrderId=1");
        // 然后模拟订单完成支付
    }

    @Test
    public void makeOrder() throws Exception {
        // 在微信端发起请求
        Login login1 = randomLogin(false);
        // 特别的设计，让这个帐号绑定到我个人微信openId 确保可以收到消息
        bindDeveloperWechat(login1);
        updateAllRunWith(login1);

        doOrder();
        // 客户也可以下单
        final String customerMobile = randomMobile();
        newRandomOrderFor(login1, login1, customerMobile);

        updateAllRunWith(loginService.byLoginName(customerMobile));
        doOrder();
    }

}