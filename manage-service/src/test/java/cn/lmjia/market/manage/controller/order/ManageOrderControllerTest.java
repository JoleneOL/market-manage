package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageMainOrderDetailPage;
import cn.lmjia.market.manage.page.ManageOrderPage;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests in error:
 * go(cn.lmjia.market.manage.controller.logistics.ManageStorageControllerTest): 找不到符合要求的Label
 *
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

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void upgradeIssue() throws Exception {
//        updateAllRunWith(newRandomManager(ManageLevel.root));
        // 所有订单里，具备当前物流的 肯定具备>0的那啥
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MainOrder> cq = cb.createQuery(MainOrder.class);
        Root<MainOrder> root = cq.from(MainOrder.class);
        entityManager.createQuery(cq
                .where(root.get(MainOrder_.currentLogistics).isNotNull())
        ).getResultList().forEach(mainOrder
                -> assertThat(mainOrder.getLogisticsSet()).isNotEmpty()
        );
    }

    /**
     * 基于海尔物流的测试，成功和失败都是基于事件的
     */
    @Test
    public void goWithHR() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        addNewHaierDepot();
        aroundTheOrder(depot -> depot instanceof HaierDepot
                , (unit, mainOrder) -> logisticsService.mockToStatus(unit.getId(), ShiftStatus.reject)
                , (unit, mainOrder) -> logisticsService.mockToStatus(unit.getId(), ShiftStatus.success));
    }

    /**
     * 基于手动物流的测试，成功和失败都是基于操作的
     */
    @Test
    public void goWithM() throws Exception {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        addNewManuallyDepot();

        aroundTheOrder(depot -> !(depot instanceof HaierDepot)
                , (unit, mainOrder) -> {
                    ManageMainOrderDetailPage page = ManageMainOrderDetailPage.of(this, driver, mainOrder.getId());
                    page.shiftDetailFor(unit.getId()).mockReject();
                }
                , (unit, mainOrder) -> {
                    ManageMainOrderDetailPage page = ManageMainOrderDetailPage.of(this, driver, mainOrder.getId());
                    page.shiftDetailFor(unit.getId()).mockSuccess();
                });
    }

    private void aroundTheOrder(Predicate<Depot> depotFilter, BiConsumer<StockShiftUnit, MainOrder> rejectWork
            , BiConsumer<StockShiftUnit, MainOrder> successWork) throws Exception {

        // 新建用户，该用户付费下单
        MainOrder order = newRandomOrderFor(randomLogin(false), randomLogin(false));

        makeOrderPay(order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("刚付款好，应该是待发货状态")
                .isEqualByComparingTo(OrderStatus.forDeliver);

        final Depot targetDepot = depotRepository.findAll().stream()
                .filter(depotFilter)
                .max(new RandomComparator()).orElseThrow(() -> new IllegalStateException("找不到何时的仓库"));

        order.getAmounts().forEach((good, integer) -> stockService.addStock(
                targetDepot
                , good.getProduct()
                , 100000, null
        ));

//        设定一样商品为我们的检测数据
        MainGood good = order.getAmounts().keySet().stream().max(new RandomComparator()).orElse(null);


        // 记录原来的库存总量
        int originStock = stockService.usableStockTotal(good.getProduct());

        log.info("原库存:" + originStock + " for:" + good.getProduct().getCode());
        // 同货品的数量
        int costTargetGoodProduct = order.getAmounts().entrySet().stream()
                .filter(entry -> entry.getKey().getProduct().equals(good.getProduct()))
                .mapToInt(Map.Entry::getValue)
                .sum();

        ManageOrderPage manageOrderPage = ManageOrderPage.of(this, driver);
        manageOrderPage.deliveryFor(order.getId()).sendAllBy(targetDepot.getName());
        // 看一下？

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
        rejectWork.accept(rejectUnit, order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("物流最终被退回 那么状态应该恢复至待发货")
                .isEqualByComparingTo(OrderStatus.forDeliver);

        manageOrderPage = ManageOrderPage.of(this, driver);
        manageOrderPage.deliveryFor(order.getId()).sendAllBy(targetDepot.getName());
        // 我们让这个物流订单成功！
        StockShiftUnit goSuccess = mainOrderService.getOrder(order.getId()).getLogisticsSet().stream()
                .filter(unit -> !Objects.equals(unit.getId(), rejectUnit.getId()))
                .findFirst().orElse(null);
        log.debug(goSuccess);
        successWork.accept(goSuccess, order);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .as("物流完成之后 订单也应该完成")
                .isEqualByComparingTo(OrderStatus.afterSale);
        mockMvc.perform(get("/manage/orderData/logistics"))
                .andDo(print());

        mockMvc.perform(get("/loginCommissionJournal").param("id", String.valueOf(order.getOrderBy().getId()))
                .accept(MediaType.TEXT_HTML_VALUE)
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

}