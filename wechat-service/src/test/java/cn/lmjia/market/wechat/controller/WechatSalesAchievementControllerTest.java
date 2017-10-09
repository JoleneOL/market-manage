package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.wechat.WechatTestBase;
import org.junit.Test;

/**
 * @author CJ
 */
public class WechatSalesAchievementControllerTest extends WechatTestBase {

    @Test
    public void go() throws Exception {
        updateAllRunWith(newRandomLogin());

        mockMvc.perform(get("/api/salesList?date=&remark=false&deal=true"))
                .andDo(print());
    }

}