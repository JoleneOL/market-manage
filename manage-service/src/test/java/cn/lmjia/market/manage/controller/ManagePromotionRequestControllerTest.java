package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 做一下页面测试
 *
 * @author CJ
 */
public class ManagePromotionRequestControllerTest extends ManageServiceTest {

    private Login current;

    @Override
    protected Login allRunWith() {
        return current;
    }



    @Test
    public void go() {
        current = newRandomManager(ManageLevel.agentManager);

        driver.get("http://localhost/managePromotionRequest");

        assertThat(driver.getTitle())
                .isEqualTo("升级合伙人申请");

    }
    @Test
    public void goReject() throws Exception {
        current = newRandomManager(ManageLevel.agentManager);
       // driver.get("http://localhost/manage/promotionRequests/1/rejected?message='你是个好人'");
        /*mockMvc.perform(put("/manage/promotionRequests/1/rejected").param("message", "你是个好人")
                .accept(MediaType.parseMediaType("application/html;charset=UTF-8")));*/

        mockMvc.perform(MockMvcRequestBuilders.put("/manage/promotionRequests/1/rejected").param("message","你是个好人")).andDo(print());

    }

}