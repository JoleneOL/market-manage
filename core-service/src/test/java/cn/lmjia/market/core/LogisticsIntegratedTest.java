package cn.lmjia.market.core;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.exception.StockOverrideException;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 物流整合测试
 * 校验物流事件可以确保订单状态的变化以及响应事件的触发
 * 相对应的是物流供应商的测试
 * 应该确保供应商的特有时间将触发特定的物流事件
 *
 * @author CJ
 */
public class LogisticsIntegratedTest extends CoreServiceTest {

    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private MainOrderService mainOrderService;

    @Test
    public void go() throws StockOverrideException, UnnecessaryShipException {
        Login login = newRandomLogin();
        MainOrder order = newRandomOrderFor(login, login);
        makeOrderPay(order);
        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .isEqualTo(OrderStatus.forDeliver);
        StockShiftUnit unit = logisticsForMainOrderFromAnyDepot(order, null, null, null, false);
        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .isEqualTo(OrderStatus.forDeliverConfirm);
        logisticsService.mockToStatus(unit.getId(), ShiftStatus.reject);
        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .isEqualTo(OrderStatus.forDeliver);
        StockShiftUnit newUnit = logisticsForMainOrderFromAnyDepot(order, null, null, null, true);
        logisticsService.mockToStatus(newUnit.getId(), ShiftStatus.success);

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .isEqualTo(OrderStatus.forInstall);
        //模拟安装
        logisticsService.mockInstallationEvent(newUnit.getId());

        assertThat(mainOrderService.getOrder(order.getId()).getOrderStatus())
                .isEqualTo(OrderStatus.afterSale);

    }

}
