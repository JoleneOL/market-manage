package cn.lmjia.market.core.controller;

import cn.lmjia.market.core.CoreWebTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.ContactWayService;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


/**
 * @author CJ
 */
public class LoginDataControllerTest extends CoreWebTest {

    @Autowired
    private ContactWayService contactWayService;

    @Test
    public void searchLoginSelect2() throws Exception {
        int currentCount = JsonPath.read(mockMvc.perform(
                get("/loginData/select2")
        )
                .andReturn().getResponse().getContentAsString(), "$.total_count");

        final int count = random.nextInt(40) + 30;
        int i = count;
        String toSearch = "";
        while (i-- > 0) {
            Login login = newRandomManager(UUID.randomUUID().toString(), ManageLevel.root);
            if (random.nextBoolean())
                contactWayService.updateMobile(login, randomMobile());
            else {
                final String name = randomString();
                contactWayService.updateName(login, name);
                toSearch = name;
            }

        }

        mockMvc.perform(
                get("/loginData/select2")
        )
                .andExpect(similarSelect2("classpath:/mock/searchLogin.json"))
                .andExpect(jsonPath("$.total_count").value(currentCount + count));

        mockMvc.perform(
                get("/loginData/select2")
                        .param("page", "2")
        )
                .andExpect(similarSelect2("classpath:/mock/searchLogin.json"))
                .andExpect(jsonPath("$.total_count").value(currentCount + count));

        // 支持搜索 搜索一个指定结果
        mockMvc.perform(
                get("/loginData/select2")
                        .param("search", toSearch)
        )
                .andExpect(similarSelect2("classpath:/mock/searchLogin.json"))
                .andExpect(jsonPath("$.total_count").value(1));
    }

}