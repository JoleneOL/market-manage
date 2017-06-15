package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.manage.ManageServiceTest;
import me.jiangcai.lib.seext.EnumUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageRoleControllerTest extends ManageServiceTest {

    private Manager myLogin;
    @Autowired
    private LoginService loginService;

    @Override
    protected Login allRunWith() {
        return myLogin;
    }

    @Test
    public void add() throws Exception {
        // 经理是无法添加的
        myLogin = newRandomManager(ManageLevel.manager);

        ManageLevel manageLevel = EnumUtils.randomEnum(ManageLevel.class, ManageLevel.agentManager);
        String loginName = randomMobile();

        // 一般等级是可以管理被经理管理的
        mockMvc.perform(
                post("/managers/add")
                        .param("loginName", loginName)
                        .param("rawPassword", randomMobile())
                        .param("level", String.valueOf(manageLevel.ordinal()))
        )
                .andExpect(status().is3xxRedirection());
        Manager manager = (Manager) loginService.byLoginName(loginName);
        assertThat(manager.getLevel())
                .isEqualTo(manageLevel);

        // 但经理是管理不了root的
        manageLevel = ManageLevel.root;
        loginName = randomMobile();
        mockMvc.perform(
                post("/managers/add")
                        .param("loginName", loginName)
                        .param("rawPassword", randomMobile())
                        .param("level", String.valueOf(manageLevel.ordinal()))
        )
                .andExpect(status().is4xxClientError());

        myLogin = newRandomManager(ManageLevel.root);


        mockMvc.perform(
                post("/managers/add")
                        .param("loginName", loginName)
                        .param("rawPassword", randomMobile())
                        .param("level", String.valueOf(manageLevel.ordinal()))
        )
                .andExpect(status().is3xxRedirection());

        manager = (Manager) loginService.byLoginName(loginName);
        assertThat(manager.getLevel())
                .isEqualTo(manageLevel);

    }

}