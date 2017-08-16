package cn.lmjia.market.core.controller.common;

import cn.lmjia.market.core.CoreWebTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class CommonSearchControllerTest extends CoreWebTest {
    @Test
    public void product() throws Exception {
        mockMvc.perform(get("/product/search")
                .param("search", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_count").isNumber())
                .andExpect(jsonPath("$.items").isArray());
    }

}