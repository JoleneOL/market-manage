package me.jiangcai.logistics;

import me.jiangcai.logistics.demo.DemoSupplier;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
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
    @Autowired
    private DemoSupplier demoSupplier;

    @Test
    public void makeShift() throws Exception {

        // 用于测试的货品，仓库
        final Thing thing = randomThing();
        final Depot depot = randomDepot();

        // 当前库存量
        int stock = logisticsService.usableStock(depot, thing.getProduct());

        // 演示一次简单发货
        logisticsService.makeShift(null, Collections.singleton(thing), depot, randomDestination());

        // 此时
        // 发货好了 库存会下降
        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());

        // 再来比入库
        StockShiftUnit unit = logisticsService.makeShift(null, Collections.singleton(thing), randomSource(), depot);

        // 虽然发起了入库 但因为没有成功入库，所以数量没有变化
        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());

        // 这是通过系统额外添加的，它们会立式生效
        logisticsService.addStock(depot, thing.getProduct(), thing.getAmount(), null);
        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock);


        // 让之前的那笔入库订单发生效果
        demoSupplier.mockEvent(unit.getId(), ShiftStatus.success);

        assertThat(logisticsService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock + thing.getAmount());
    }

}