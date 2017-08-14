package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
public class AgentDataControllerTest extends DealerServiceTest {

    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private ReadService readService;

    @Test
    public void list() throws Exception {
        newRandomAgentSystemAnd(UUID.randomUUID().toString(), (login, agent) -> {
            System.out.println(login);
            System.out.println(agentService.agentLevel(agent));
            // 没有登录 那么怎么使用这个标签呢？
            runWith(login, () -> {

                mockMvc.perform(
                        get("/agentData/listRuling")
                )
//                        .andDo(print())
                        .andExpect(similarSelect2("classpath:/dealer-view/mock/agentList.json"));


                agentDataList(null);
                // 支持搜索条件 agentName 可以是手机号码 也可以是用户名 也可以是rankName
                AgentLevel target = agentLevelRepository.getOne(agent.getId()).getSubAgents().stream()
                        .max(new RandomComparator()).orElse(null);
                //登录名
                agentDataList(builder
                        -> builder.param("agentName", target.getLogin().getLoginName())
                )
                        .andExpect(jsonPath("$.data.length()").value(1));

                // 级别名称
                agentDataList(builder
                        -> builder.param("agentName", target.getRank())
                )
                        .andExpect(jsonPath("$.data.length()").value(1));

                String name = readService.nameForPrincipal(target.getLogin());
                // 名字
                agentDataList(builder
                        -> builder.param("agentName", name)
                )
                        .andExpect(jsonPath("$.data.length()").value(1));

                String mobile = readService.mobileFor(target.getLogin());

                if (!StringUtils.isEmpty(mobile)) {
                    agentDataList(builder
                            -> builder.param("agentName", mobile)
                    )
                            .andExpect(jsonPath("$.data.length()").value(1));
                }

                return null;
            });
        });

//        agentLevelRepository.findAll().forEach(agentLevel -> {
//            System.out.println(agentLevel);
//            System.out.println(agentService.agentLevel(agentLevel));
//        });

        Manager manager = newRandomManager("", ManageLevel.root);
        runWith(manager, () -> {
            mockMvc.perform(
                    get("/agentData/listRuling")
            )
//                    .andDo(print())
                    .andExpect(similarSelect2("classpath:/dealer-view/mock/agentList.json"));
            return null;
        });

    }

}