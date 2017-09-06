package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.manage.ManageServiceTest;
import com.jayway.jsonpath.JsonPath;
import me.jiangcai.lib.test.matcher.NumberMatcher;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import me.jiangcai.logistics.repository.DepotRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ActiveProfiles("mysql2")
public class ManageOrderControllerTest extends ManageServiceTest {

    private static final Log log = LogFactory.getLog(ManageOrderControllerTest.class);
    @Autowired
    private StockService stockService;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private LogisticsService logisticsService;

    @Test
    public void upgradeIssue() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        mockMvc.perform(get("/manage/orderData/logistics"))
                .andDo(print());
    }

    @Test
    public void go() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));

        // 新建用户，该用户付费下单
        MainOrder order = newRandomOrderFor(randomLogin(false), randomLogin(false));

//        stockService.enabledUsableStockInfo(((productPath, criteriaBuilder) -> criteriaBuilder.equal(productPath, order.getGood().getProduct())), null);

        printOrderDetail(order);
        makeOrderPay(order);
        printOrderDetail(order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("刚付款好，应该是待发货状态")
                .isEqualByComparingTo(OrderStatus.forDeliver);
        // 假定当前无货 所以应该看不到任何可用仓库
//        mockMvc.perform(get("/orderData/logistics/" + String.valueOf(order.getId())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.depots.length()").value(0));
        // 首先得有仓库
        addNewHaierDepot();

        final Depot targetDepot = depotRepository.findAll().stream()
                .filter(depot -> depot instanceof HaierDepot)
                .max(new RandomComparator()).orElse(null);

        order.getAmounts().forEach((good, integer) -> stockService.addStock(
                targetDepot
                , good.getProduct()
                , 100000, null
        ));

//        设定一样商品为我们的检测数据
        MainGood good = order.getAmounts().keySet().stream().max(new RandomComparator()).orElse(null);


        // 记录原来的库存总量
        int originStock = stockService.usableStockTotal(good.getProduct());

        log.info("原库存:" + originStock);
        // 同货品的数量
        int costTargetGoodProduct = order.getAmounts().entrySet().stream()
                .filter(entry -> entry.getKey().getProduct().equals(good.getProduct()))
                .mapToInt(Map.Entry::getValue)
                .sum();
        String responseString = mockMvc.perform(get("/orderData/logistics/" + String.valueOf(order.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depots.length()").value(NumberMatcher.numberGreatThanOrEquals(1)))
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> depots = JsonPath.read(responseString, "$.depots");

        // 获得了仓库 现在执行发货
        mockMvc.perform(put("/orderData/logistics/" + String.valueOf(order.getId()))
                .contentType(MediaType.TEXT_PLAIN)
                .content(String.valueOf(depots.get(0).get("id")))
        )
                .andExpect(status().is2xxSuccessful());

        printOrderDetail(order);

        assertThat(stockService.usableStockTotal(good.getProduct()))
                .as("执行发货之后，可用库存应该减少")
                .isEqualTo(originStock - costTargetGoodProduct);

        // 断言库存量 应该减少了 暂时跳过
        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("调整为 已发货 并且等待物流的状态")
                .isEqualByComparingTo(OrderStatus.forDeliverConfirm);
        assertThat(mainOrderService.getOrder(order.getId()).getLogisticsSet())
                .as("肯定存在物流记录")
                .isNotEmpty();

        // 那么物流订单失败之后呢？
        StockShiftUnit rejectUnit = mainOrderService.getOrder(order.getId()).getLogisticsSet().iterator().next();
        logisticsService.mockToStatus(rejectUnit.getId(), ShiftStatus.reject);
        printOrderDetail(order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("物流最终被退回 那么状态应该恢复至待发货")
                .isEqualByComparingTo(OrderStatus.forDeliver);

        // 重新发货
        mockMvc.perform(put("/orderData/logistics/" + String.valueOf(order.getId()))
                .contentType(MediaType.TEXT_PLAIN)
                .content(String.valueOf(depots.get(0).get("id")))
        )
                .andExpect(status().is2xxSuccessful());
// 我们让这个物流订单成功！
        StockShiftUnit goSuccess = mainOrderService.getOrder(order.getId()).getLogisticsSet().stream()
                .filter(unit -> !Objects.equals(unit.getId(), rejectUnit.getId()))
                .findFirst().orElse(null);
        log.debug(goSuccess);
        logisticsService.mockToStatus(goSuccess.getId(), ShiftStatus.success);

        printOrderDetail(order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("物流完成之后 订单也应该完成")
                .isEqualByComparingTo(OrderStatus.forInstall);


        mockMvc.perform(get("/manage/orderData/logistics"))
                .andDo(print());
    }

    private void printOrderDetail(MainOrder order) throws Exception {
        mockMvc.perform(get("/mainOrderDetail").param("id", String.valueOf(order.getId())))
                .andExpect(status().isOk());
    }

}