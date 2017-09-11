package me.jiangcai.logistics;

import me.jiangcai.logistics.demo.DemoProject;
import me.jiangcai.logistics.demo.entity.DemoOrder;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles({"mysql2", "h2file2"})
public class LogisticsServiceTest extends LogisticsTestBase {

    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private StockService stockService;
    @Autowired
    private DemoProject demoProject;

    @Test
    public void order1() {
        order(false);
    }

    @Test
    public void order2() {
        order(true);
    }

    private void order(boolean testInstall) {
        Map<Product, Integer> amounts = randomProductAmount();
        // 综合订单测试
        // 创建一个需要物流多个货品的订单
        DemoOrder order = demoProject.createOrder(amounts);

        // 逐个货品的创建物流订单（都无需安装）
        demoProject.cleanEvents();
        final List<StockShiftUnit> unitList = new ArrayList<>();
        amounts.forEach(((product, integer) -> {
            StockShiftUnit unit = demoProject.work(order, product, integer, testInstall, randomSource(), randomDestination());
            unitList.add(unit);
        }));
        // 只有在所有货物抵达之后收到 OrderDeliveredEvent 事件
        for (int i = 1; i < unitList.size(); i++) {
            logisticsService.mockToStatus(unitList.get(i).getId(), ShiftStatus.success);
            assertThat(demoProject.lastOrderDeliveredEvent())
                    .as("只有在所有货物抵达之后收到 OrderDeliveredEvent 事件")
                    .isNull();
        }
        logisticsService.mockToStatus(unitList.get(0).getId(), ShiftStatus.success);
        assertThat(demoProject.lastOrderDeliveredEvent())
                .as("只有在所有货物抵达之后收到 OrderDeliveredEvent 事件")
                .isNotNull();
        assertThat(demoProject.lastOrderDeliveredEvent().getOrder())
                .as("只有在所有货物抵达之后收到 OrderDeliveredEvent 事件")
                .isEqualTo(order);
        if (!testInstall)
            // 因无需安装同时也可以接受到 OrderInstalledEvent
            assertThat(demoProject.lastOrderInstalledEvent())
                    .as("因无需安装同时也可以接受到 OrderInstalledEvent")
                    .isNotNull();
        else {
            assertThat(demoProject.lastOrderInstalledEvent())
                    .as("安装尚未完成")
                    .isNull();
            for (int i = 1; i < unitList.size(); i++) {
                logisticsService.mockInstallationEvent(unitList.get(i).getId());
                assertThat(demoProject.lastOrderInstalledEvent())
                        .as("所有安装都完成 才算完成")
                        .isNull();
            }

            logisticsService.mockInstallationEvent(unitList.get(0).getId());
            assertThat(demoProject.lastOrderInstalledEvent())
                    .as("都完成了！")
                    .isNotNull();
        }

        // 同样的测试 区别仅仅是物流订单是需要安装的
        // 那么只有在所有货物抵达之后收到 OrderDeliveredEvent 事件
        // 然后在安装也完成之后接受到 OrderInstalledEvent
    }

    private Map<Product, Integer> randomProductAmount() {
        HashMap<Product, Integer> map = new HashMap<>();
        int count = 2 + random.nextInt(4);
        while (count-- > 0) {
            Product p1 = randomNewProduct();
            map.put(p1, 1 + random.nextInt(5));
        }

        return map;
    }

    @Test
    public void makeShift() throws Exception {

        // 用于测试的货品，仓库
        final Thing thing = randomThing();
        final Depot depot = randomDepot();

        stockService.usableStock();

        // 当前库存量
        int stock = stockService.usableStock(depot, thing.getProduct());

        // 演示一次简单发货
        logisticsService.makeShift(null, null, Collections.singleton(thing), depot, randomDestination());

        // 此时
        // 发货好了 库存会下降
        assertThat(stockService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());

        // 再来比入库
        StockShiftUnit unit = logisticsService.makeShift(null, null, Collections.singleton(thing), randomSource(), depot);

        // 虽然发起了入库 但因为没有成功入库，所以数量没有变化
        assertThat(stockService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock - thing.getAmount());

        // 这是通过系统额外添加的，它们会立式生效
        stockService.addStock(depot, thing.getProduct(), thing.getAmount(), null);
        assertThat(stockService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock);


        // 让之前的那笔入库订单发生效果
        logisticsService.mockToStatus(unit.getId(), ShiftStatus.success);

        assertThat(stockService.usableStock(depot, thing.getProduct()))
                .isEqualTo(stock + thing.getAmount());

        assertThat(stockService.usableStock()
                .forOne(thing.getProduct(), depot))
                .isEqualTo(stock + thing.getAmount());
// 特定条件 并且是 enable
        assertThat(stockService.enabledUsableStockInfo(
                (productPath, criteriaBuilder) -> criteriaBuilder.equal(productPath, thing.getProduct())
                , (depotJoin, criteriaBuilder) -> criteriaBuilder.equal(depotJoin, depot))
                .forOne(thing.getProduct(), depot))
                .isEqualTo(stock + thing.getAmount());
    }

}