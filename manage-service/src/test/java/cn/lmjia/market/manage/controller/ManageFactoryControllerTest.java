package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Factory;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.FactoryRepository;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageFactoryControllerTest extends ManageServiceTest {

    @Autowired
    private FactoryRepository factoryRepository;

    @Override
    protected Login allRunWith() {
        return newRandomManager(ManageLevel.root);
    }

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/manageFactory");
        assertThat(driver.getTitle())
                .isEqualTo("工厂管理");
        driver.get("http://localhost/manageFactoryAdd");
        assertThat(driver.getTitle())
                .isEqualTo("新工厂");
    }

    @Test
    public void data() throws Exception {
        add();
        mockMvc.perform(
                get("/manage/factoryList")
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(similarJQueryDataTable("classpath:/manage-view/mock/factoryList.json"));
    }

    @Test
    public void add() throws Exception {
        addNewFactory();
    }

    @Test
    public void disable() throws Exception {
        changeEnable("disable", false);
    }

    private void changeEnable(String path, boolean targetEnable) throws Exception {
        add();
        // 随便找一个工厂
        Factory factory = factoryRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        factory.setEnable(!targetEnable);
        factoryRepository.save(factory);

        mockMvc.perform(
                put("/manage/factoryList/" + factory.getId() + "/" + path)
        )
                .andExpect(status().is2xxSuccessful());

        assertThat(factoryRepository.getOne(factory.getId()).isEnable())
                .isEqualTo(targetEnable);
    }

    @Test
    public void enable() throws Exception {
        changeEnable("enable", true);
    }

}