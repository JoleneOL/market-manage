package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author CJ
 */
public class WechatMainOrderControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatMainOrderControllerTest.class);

    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Test
    public void makeOrder() throws Exception {
        // 在微信端发起请求
        Login login1 = randomLogin(false);
        runWith(login1, () -> {
            mockMvc.perform(wechatGet("/wechatOrder"))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("wechat@orderPlace.html"));
            // 这波则是开干了
            Address address = randomAddress();
            MainGood good = mainGoodRepository.findAll().stream().max(new RandomComparator()).orElse(null);
            String code = random.nextBoolean() ? null : UUID.randomUUID().toString().replaceAll("-", "");
            Login recommend = randomLogin(true);
            //
            while (true) {
                try {
                    String result = mockMvc.perform(
                            wechatPost("/wechatOrder")
                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                    .param("name", "W客户" + RandomStringUtils.randomAlphabetic(6))
                                    .param("age", String.valueOf(20 + random.nextInt(50)))
                                    .param("gender", String.valueOf(1 + random.nextInt(2)))
                                    .param("address", address.getStandardWithoutOther())
                                    .param("fullAddress", address.getOtherAddress())
                                    .param("mobile", randomMobile())
                                    .param("goodId", String.valueOf(good.getId()))
                                    .param("leasedType", good.getProduct().getCode())
                                    .param("amount", String.valueOf(1 + random.nextInt(10)))
                                    .param("activityCode", code)
                                    .param("recommend", String.valueOf(recommend.getId()))
                    )
                            .andExpect(status().is3xxRedirection())
                            .andReturn().getResponse().getHeader("Location");

                    // 使用 driver 打开!
                    driver.get("http://localhost" + result);
//                    mockMvc.perform(wechatGet("/paySuccess?mainOrderId=1"))
//                            .andDo(print());
                    PaySuccessPage page = PaySuccessPage.waitingForSuccess(this, driver, 3);
                    // 然后模拟订单完成支付
                    return null;
                } catch (AssertionError error) {
                    if (!error.getMessage().contains("200"))
                        throw error;
                    log.info("那就再试一次呗");
                    Thread.sleep(3000);
                }

            }
        });
    }

}