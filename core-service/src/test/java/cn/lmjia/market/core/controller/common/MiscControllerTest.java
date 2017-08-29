package cn.lmjia.market.core.controller.common;

import cn.lmjia.market.core.CoreWebTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class MiscControllerTest extends CoreWebTest {

    @Autowired
    private LoginService loginService;

    @Test
    public void sendRegisterCode() throws Exception {
        // 一种成功
        mockMvc.perform(post("/misc/sendRegisterCode")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("mobile", randomMobile())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200));
        // 一种失败
        final String mobile = randomMobile();
        loginService.newLogin(Login.class, mobile, newRandomLogin(), UUID.randomUUID().toString());

        mockMvc.perform(post("/misc/sendRegisterCode")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("mobile", mobile)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(401));

    }

}