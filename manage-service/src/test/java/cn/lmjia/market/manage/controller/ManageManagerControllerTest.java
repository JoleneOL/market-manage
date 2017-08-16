package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.manage.ManageServiceTest;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageManagerControllerTest extends ManageServiceTest {

    private Login current;
    @Autowired
    private LoginRepository loginRepository;

    @Before
    public void before() {
        current = newRandomManager(ManageLevel.root);
    }

    @Override
    protected Login allRunWith() {
        return current;
    }

    @Test
    public void pages() throws Exception {
        driver.get("http://localhost/manageManagerAdd");
        System.out.println(driver.getPageSource());
        addUser();
        // manageManagerEdit
        // 随便找一个来编辑
        Login lastOne = loginRepository.findAll(new Sort(Sort.Direction.DESC, "id")).get(0);
        driver.get("http://localhost/manageManagerEdit?id=" + lastOne.getId());
        System.out.println(driver.getPageSource());
        assertThat(true)
                .isTrue();
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
        assertThat(true)
                .isTrue();
    }

    @Test
    public void addUser() throws Exception {
        final String loginName = randomMobile();
        // 随便给几个权限
        Collection<ManageLevel> levelSet = randomLevel();
        mockMvc.perform(
                paramLevel(post("/manage/managers")
                        .param("name", loginName)
                        .param("department", RandomStringUtils.randomAlphabetic(10))
//                        .param("department","运营部")
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

    private RequestBuilder paramLevel(MockHttpServletRequestBuilder builder, Collection<ManageLevel> levelSet) {
        MockHttpServletRequestBuilder newBuilder = builder;
        for (ManageLevel level : levelSet) {
            newBuilder = newBuilder.param("role", level.name());
        }
        return newBuilder;
    }

    private Collection<ManageLevel> randomLevel() {
//        return Collections.singleton(EnumUtils.randomEnum(ManageLevel.class));
        return Arrays.asList(
                ManageLevel.manager, ManageLevel.agentManager
        );
    }

}