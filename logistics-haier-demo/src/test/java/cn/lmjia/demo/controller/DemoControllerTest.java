package cn.lmjia.demo.controller;

import cn.lmjia.demo.config.DemoConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = DemoConfig.class)
@WebAppConfiguration
public class DemoControllerTest extends SpringWebTest {

    @Test
    public void index() throws Exception {
        String uri = mockMvc.perform(get(""))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());
    }

    @Test
    public void go() {
        // 我们的流程应该是 添加产品

    }

}