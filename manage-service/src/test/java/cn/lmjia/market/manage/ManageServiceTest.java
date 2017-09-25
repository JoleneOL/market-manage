package cn.lmjia.market.manage;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.manage.config.ManageConfig;
import me.jiangcai.jpa.entity.support.Address;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {ManageConfig.class, SecurityConfig.class, ManageServiceTest.Config.class})
public abstract class ManageServiceTest extends DealerServiceTest {

    /**
     * 增加一个日日顺仓库
     *
     * @throws Exception
     */
    protected void addNewHaierDepot() throws Exception {
        Address address = randomAddress();
        mockMvc.perform(
                post("/manage/depotList")
                        .param("type", "HaierDepot")
                        .param("name", RandomStringUtils.randomAlphabetic(10) + "海尔仓库")
                        .param("address", address.getStandardWithoutOther())
                        .param("fullAddress", address.getOtherAddress())
                        .param("chargePeopleName", RandomStringUtils.randomAlphabetic(3) + "人")
                        .param("chargePeopleMobile", randomMobile())
                        .param("haierCode", RandomStringUtils.randomAlphabetic(31))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("location", "/manageDepot"));
    }

    protected void addNewFactory() throws Exception {
        Address address = randomAddress();
        mockMvc.perform(
                post("/manage/factoryList")
                        .param("name", RandomStringUtils.randomAlphabetic(20) + "工厂")
                        .param("address", address.getStandardWithoutOther())
                        .param("fullAddress", address.getOtherAddress())
                        .param("chargePeopleName", RandomStringUtils.randomAlphabetic(3) + "人")
                        .param("chargePeopleMobile", randomMobile())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("location", "/manageFactory"));
    }

    protected void addNewTag() throws Exception {
        mockMvc.perform(
                post("/manage/tagList")
                        .param("name", RandomStringUtils.randomAlphabetic(10) + "标签")
                        .param("type", String.valueOf(random.nextInt(1)))
                        .param("weight", String.valueOf(random.nextInt(100)))
                        .param("icon", newRandomImagePath()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("location", "/manageTag"));
    }

    @Configuration
    public static class Config {
    }
}
