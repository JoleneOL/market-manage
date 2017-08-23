package cn.lmjia.market.core.model;

import cn.lmjia.market.core.CoreWebTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class MainGoodsAndAmountsTest extends CoreWebTest {

    @Test
    public void go() throws Exception {
        mockMvc.perform(post("/MainGoodsAndAmountsTestController")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("goods", "1,1")
                .param("goods", "2,1")
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        mockMvc.perform(post("/MainGoodsAndAmountsTestController")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("goods", "1,1")
//                .param("goods", "2,1")
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

}