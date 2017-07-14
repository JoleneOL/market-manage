package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class WechatUpgradeControllerTest extends WechatShareControllerTest {

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainOrderRepository mainOrderRepository;

    @Test
    public void upgrade() throws Exception {
        // 找一个新晋的login
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        currentLogin = user;

        driver.get("http://localhost/wechatUpgrade");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("我的下单");

        // 去买个东西吧
        orderRequestBuilder(wechatPost("/wechatOrder"), randomOrderRequest());
        MainOrder order = mainOrderRepository.findAll((root, query, cb) -> cb.equal(root.get("orderBy"), user)).get(0);
        // 支付订单
        makeOrderPay(order);
        makeOrderDone(order);

        // 现在可以开始了
        driver.get("http://localhost/wechatUpgrade");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("我要升级");


        int level = 1;
        Address address = randomAddress();
        String cardFrontPath = newRandomImagePath();
        String cardBackPath = newRandomImagePath();
        String businessLicensePath = newRandomImagePath();
        // upgradeMode
        String payUri = mockMvc.perform(wechatPost("/wechatUpgrade")
                .param("newLevel", String.valueOf(level))
                .param("address", address.getStandardWithoutOther())
                .param("fullAddress", address.getOtherAddress())
                .param("cardFrontPath", cardFrontPath)
                .param("cardBackPath", cardBackPath)
                .param("businessLicensePath", businessLicensePath)
        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + payUri);
        PaySuccessPage.waitingForSuccess(this, driver, 3);

        // 这个时候业务算是完成了；我们可以看到后端请求了
        // TODO
        // 我们批准它
        // 断言等级
        // 然后继续升级
        // 断言申请
        // 再批准
        // 断言等级
        // 然后继续升级
        // 断言申请
        // 再批准
        // 断言等级
    }

}