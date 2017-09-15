package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.repository.DepotRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageDepotControllerTest extends ManageServiceTest {

    @Autowired
    private DepotRepository depotRepository;

    @Override
    protected Login allRunWith() {
        return newRandomManager(ManageLevel.root);
    }

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/manageDepot");
        assertThat(driver.getTitle())
                .isEqualTo("仓库管理");
        driver.get("http://localhost/manageDepotAdd");
        assertThat(driver.getTitle())
                .isEqualTo("新仓库");
    }

    @Test
    public void data() throws Exception {
        add();
        mockMvc.perform(
                get("/manage/depotList")
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(similarJQueryDataTable("classpath:/manage-view/mock/depotList.json"));
    }

    @Test
    public void add() throws Exception {
        addNewHaierDepot();
        addNewManuallyDepot();
    }

    @Test
    public void disable() throws Exception {
        changeEnable("disable", false);
    }

    private void changeEnable(String path, boolean targetEnable) throws Exception {
        add();
        // 随便找一个仓库
        Depot depot = depotRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        depot.setEnable(!targetEnable);
        depotRepository.save(depot);

        mockMvc.perform(
                put("/manage/depotList/" + depot.getId() + "/" + path)
        )
                .andExpect(status().is2xxSuccessful());

        assertThat(depotRepository.getOne(depot.getId()).isEnable())
                .isEqualTo(targetEnable);
    }

    @Test
    public void enable() throws Exception {
        changeEnable("enable", true);
    }

}