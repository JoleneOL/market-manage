package cn.lmjia.market.web.controller;

import org.junit.Test;

/**
 * @author CJ
 */
public class WelcomeControllerTest extends WebTest {
    @Test
    public void index() throws Exception {
        mockMvc.perform(get(""))
                .andDo(print());
    }

}