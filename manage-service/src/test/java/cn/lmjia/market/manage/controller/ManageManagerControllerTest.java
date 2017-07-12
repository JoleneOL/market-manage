package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import me.jiangcai.lib.seext.EnumUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageManagerControllerTest extends ManageServiceTest {

    private Login current;

    @Before
    public void before() {
        current = newRandomManager(ManageLevel.root);
    }

    @Override
    protected Login allRunWith() {
        return current;
    }

    @Test
    public void list() throws Exception {
        // /manage/managers
        addUser();

        mockMvc.perform(
                get("/manage/managers")
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void addUser() throws Exception {
        final String loginName = randomMobile();
        // 随便给几个权限
        Set<ManageLevel> levelSet = randomLevel();
        mockMvc.perform(
                paramLevel(post("/manage/managers")
                        .param("name", loginName)
                        .param("department", RandomStringUtils.randomAlphabetic(10))
                        .param("realName", RandomStringUtils.randomAlphabetic(10))
                        .param("enable", "1")
                        .param("comment", RandomStringUtils.randomAlphabetic(10)), levelSet)
        )
                .andExpect(status().is3xxRedirection());

        // 新用户已建立
        Manager manager = (Manager) loginService.byLoginName(loginName);
        assertThat(manager)
                .isNotNull();
        assertThat(manager.getLevelSet())
                .containsOnlyElementsOf(levelSet);
        assertThat(manager.isEnabled())
                .isTrue();
    }

    private RequestBuilder paramLevel(MockHttpServletRequestBuilder builder, Set<ManageLevel> levelSet) {
        for (ManageLevel level : levelSet) {
            builder = builder.param("role", level.name());
        }
        return builder;
    }

    private Set<ManageLevel> randomLevel() {
        return Collections.singleton(EnumUtils.randomEnum(ManageLevel.class));
    }

}