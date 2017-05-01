package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author CJ
 */
public class AgentDataControllerTest extends DealerServiceTest {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

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
                    // 支持搜索条件 agentName 可以是手机号码 也可以是用户名 也可以是rankName
                    AgentLevel agentLevel = agentLevelRepository.getOne(agent.getId());
                    

                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}