package cn.lmjia.market.manage;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.manage.config.ManageConfig;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.haier.HaierSupplier;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.function.Consumer;

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

    @Configuration
    public static class Config {
        @Bean
        @Primary
        public HaierSupplier haierSupplier() {
            return new HaierSupplier() {
                @Override
                public void cancelOrder(String id, boolean focus, String reason) {

                }

                @Override
                public void updateProduct(Product product) {

                }

                @Override
                public String sign(String content, String keyValue) {
                    return null;
                }

                @Override
                public Object event(String businessType, String source, String contentType, String sign, String content) throws IOException {
                    return null;
                }

                @Override
                public StockShiftUnit makeShift(LogisticsSource source, LogisticsDestination destination, Consumer<StockShiftUnit> forUnit, int options) {
                    StockShiftUnit unit = new StockShiftUnit();
                    forUnit.accept(unit);
                    //
                    return unit;
                }
            };
        }
    }
}
