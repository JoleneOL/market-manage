package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;

import java.util.UUID;

/**
 * @author CJ
 */
public class AgentDataControllerTest extends DealerServiceTest {
    @Test
    public void list() throws Exception {
        newRandomAgentSystemAnd(UUID.randomUUID().toString(), (login, agent) -> {
            System.out.println(login);
            System.out.println(agentService.agentLevel(agent));
            // 没有登录 那么怎么使用这个标签呢？
            try {
                runWith(login, () -> {
                    mockMvc.perform(
                            get("/agentData/list")
                    )
//                            .andDo(print())
                            .andExpect(similarJQueryDataTable("classpath:/mock/agentData.json"));

                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}