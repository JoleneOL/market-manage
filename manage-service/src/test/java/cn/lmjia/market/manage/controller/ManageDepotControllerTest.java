package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.DepotRepository;
import cn.lmjia.market.manage.ManageServiceTest;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.entity.Depot;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        Address address = randomAddress();
        mockMvc.perform(
                post("/manage/depotList")
                        .param("name", RandomStringUtils.randomAlphabetic(99))
                        .param("address", address.getStandardWithoutOther())
                        .param("fullAddress", address.getOtherAddress())
                        .param("chargePeopleName", randomMobile())
                        .param("chargePeopleMobile", randomMobile())
//                        .param("haierCode", RandomStringUtils.randomAlphabetic(31))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("location", "/manageDepot"));

        addNewHaierDepot();
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