package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import com.jayway.jsonpath.JsonPath;
import me.jiangcai.jpa.entity.support.Address;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ActiveProfiles("mysql2")
public class WechatUpgradeControllerTest extends WechatTestBase {

    @Autowired
    private ReadService readService;

    @Test
    public void upgrade1() throws Exception {
        upgrade(1, 4);
    }

    @Test
    public void upgrade2() throws Exception {
        upgrade(2, 3);
    }

    @Test
    public void upgrade3() throws Exception {
        upgrade(3, 2);
    }

    private void upgrade(int level, int targetLevel) throws Exception {
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
//                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getHeader("Location");

//        if (level == 1){
//            System.out.println("for rollback checking..!!!");
//            Thread.sleep(Long.MAX_VALUE);
//        }

        driver.get("http://localhost" + payUri);
        PaySuccessPage.waitingForSuccess(this, driver, 3, "http://localhost/wechatUpgradeApplySuccess");

        // 这个时候业务算是完成了；我们可以看到后端请求了
        assertExistingRequest(user);
        // 我们批准它
        approvedOnlyRequest(user, "我的省代理");
        //拒绝他
        //goReject(user, "你是个好人);
        // 断言等级
        assertThat(readService.agentLevelForPrincipal(user)).isEqualTo(targetLevel);

        // 然后继续升级
        // 断言申请
        // 再批准
        // 断言等级
        // 然后继续升级
        // 断言申请
        // 再批准
        // 断言等级
    }

    private void approvedOnlyRequest(Login user, String title) throws Exception {
        runWith(newRandomManager(ManageLevel.root), () -> {
            Number id = JsonPath.read(mockMvc.perform(
                    get("/manage/promotionRequests")
                            .param("mobile", readService.mobileFor(user))
            )
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.data[0].id");
            mockMvc.perform(put("/manage/promotionRequests/" + id + "/approved")
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .content(title)
            )
                    .andExpect(status().isNoContent());
            return null;
        });
    }

    private void assertExistingRequest(Login user) throws Exception {
        runWith(newRandomManager(ManageLevel.root), () -> {
            mockMvc.perform(
                    get("/manage/promotionRequests")
                            .param("mobile", readService.mobileFor(user))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1));
            return null;
        });

    }

    private void goReject(Login user, String title) throws Exception {
        // driver.get("http://localhost/manage/promotionRequests/1/rejected?message='你是个好人'");
        /*mockMvc.perform(put("/manage/promotionRequests/1/rejected").param("message", "你是个好人")
                .accept(MediaType.parseMediaType("application/html;charset=UTF-8")));*/

        runWith(newRandomManager(ManageLevel.root), () -> {
            Number id = JsonPath.read(mockMvc.perform(
                    get("/manage/promotionRequests")
                            .param("mobile", readService.mobileFor(user))
            )
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.data[0].id");
            mockMvc.perform(MockMvcRequestBuilders.put("/manage/promotionRequests/" + id + "/rejected")
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .content(title))
            .andExpect(status().is2xxSuccessful());
            return null;
        });
    }
}