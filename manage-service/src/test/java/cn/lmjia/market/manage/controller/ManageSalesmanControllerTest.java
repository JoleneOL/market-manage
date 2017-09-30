package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.SalesmanService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageSalesmanPage;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class ManageSalesmanControllerTest extends ManageServiceTest {

    @Autowired
    private SalesmanService salesmanService;

    @Test
    public void go() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));

        ManageSalesmanPage page = ManageSalesmanPage.of(this, driver);

        Login newOne = newRandomLogin();
        page.addSalesman(newOne.getLoginName());

        Thread.sleep(1000L);
        page.waitForTable();

        // 可以调整比例 调整等级 以及设定是否启用
        final double targetRate = Math.abs(random.nextDouble());
        mockMvc.perform(put("/manage/salesmen/" + newOne.getId() + "/rate")
                .contentType(MediaType.TEXT_PLAIN)
                .content(String.valueOf(targetRate))
        )
                .andExpect(status().is2xxSuccessful());

        assertThat(salesmanService.get(newOne.getId()).getSalesRate())
                .isCloseTo(new BigDecimal(targetRate), Offset.offset(new BigDecimal("0.00001")));
        // rank
        final String targetRank = RandomStringUtils.randomAlphabetic(7);
        mockMvc.perform(put("/manage/salesmen/" + newOne.getId() + "/rank")
                .contentType(MediaType.TEXT_PLAIN)
                .content(targetRank)
        )
                .andExpect(status().is2xxSuccessful());

        assertThat(salesmanService.get(newOne.getId()).getRank())
                .isEqualToIgnoringCase(targetRank);


        // disable and enable
        mockMvc.perform(put("/manage/salesmen/" + newOne.getId() + "/disable")
        )
                .andExpect(status().is2xxSuccessful());
        assertThat(salesmanService.get(newOne.getId()).isEnable()).isFalse();

        mockMvc.perform(put("/manage/salesmen/" + newOne.getId() + "/enable")
        )
                .andExpect(status().is2xxSuccessful());
        assertThat(salesmanService.get(newOne.getId()).isEnable()).isTrue();

    }

}