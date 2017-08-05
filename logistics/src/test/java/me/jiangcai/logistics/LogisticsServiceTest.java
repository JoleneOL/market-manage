package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
//@ActiveProfiles("mysql")
public class LogisticsServiceTest extends LogisticsTestBase {

    @Autowired
    private LogisticsService logisticsService;

    @Test
    public void makeShift() throws Exception {
        System.out.println(logisticsService);

        final Thing thing = randomThing();
        final Depot depot = randomDepot();

        int stock = logisticsService.usableStock(depot, thing.getProduct());

        // 演示一次简单发货
        logisticsService.makeShift(null, Collections.singleton(thing), depot, randomDestination());

        // 此时
        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());

        // 再来比入库
        logisticsService.makeShift(null, Collections.singleton(thing), randomSource(), depot);

        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());
        // TODO 要组织事件了

        logisticsService.addStock(depot, thing.getProduct(), thing.getAmount(), null);
        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock);
    }

}