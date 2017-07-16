package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.manage.config.ManageConfig;
import cn.lmjia.market.manage.controller.ManagePromotionRequestController;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {ManageConfig.class, SecurityConfig.class})
public class WechatUpgradeControllerTest extends WechatTestBase {

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private ReadService readService;
    @Autowired
    private ManagePromotionRequestController managePromotionRequestController;
    @Autowired
    private PromotionRequestRepository promotionRequestRepository;

    @Test
    public void upgrade() throws Exception {
        // 找一个新晋的login
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        driver.get("http://localhost/wechatUpgrade");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("我的下单");

        // 去买个东西吧
        makeSuccessOrder(user);

        // 现在可以开始了
        driver.get("http://localhost/wechatUpgrade");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("我要升级");


        int level = 1;
        String agentName = RandomStringUtils.randomAlphabetic(10);
        Address address = randomAddress();
        String cardFrontPath = newRandomImagePath();
        String cardBackPath = newRandomImagePath();
        String businessLicensePath = newRandomImagePath();
        // upgradeMode
        String payUri = mockMvc.perform(wechatPost("/wechatUpgrade")
                .param("agentName", agentName)
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
        assertExistingRequest(user);
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

    private void assertExistingRequest(Login user) throws Exception {
        runWith(newRandomManager(ManageLevel.root), () -> {
            assertThat(promotionRequestRepository.findAll(managePromotionRequestController.data(null, readService.mobileFor(user))
                    .specification()))
                    .isNotEmpty();
            return null;
        });

    }

}