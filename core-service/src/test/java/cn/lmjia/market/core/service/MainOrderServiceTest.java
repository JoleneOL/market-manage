package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-08-21.
 */
public class MainOrderServiceTest extends CoreServiceTest {
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private SystemStringService systemStringService;

    private Login testLogin;

    @Before
    public void startTest() {
        testLogin = randomLogin(false);
    }

    @Test
    public void lockedStock() throws Exception {
        final Login order = testLogin;
        List<MainGood> saleGoodList = mainGoodService.forSale();
        Map<Long, Integer> lockStockMap = new HashMap<>();

        //如果货品已经存在订单了，就先给他一个初始值
        saleGoodList.forEach(mainGood -> {
            int lockStock = mainOrderService.sumProductNum(mainGood.getProduct());
            lockStockMap.put(mainGood.getId(),lockStock);
        });

        //未支付订单
        MainOrder forPayOrder = newRandomOrderFor(order, order);
        assertEquals(OrderStatus.forPay, forPayOrder.getOrderStatus());
        saleGoodList.forEach(mainGood -> {
            Optional<MainGood> amountMainGood = forPayOrder.getAmounts().keySet().stream().filter(p -> p.getId().equals(mainGood.getId())).findAny();
            if (amountMainGood.isPresent()) {
                int exceptLockStock = forPayOrder.getAmounts().get(amountMainGood.get());
                assertEquals(lockStockMap.get(mainGood.getId()) + exceptLockStock, mainOrderService.sumProductNum(mainGood.getProduct()));
                lockStockMap.put(mainGood.getId(), lockStockMap.get(mainGood.getId()) + exceptLockStock);
            } else {
                assertEquals(0, mainOrderService.sumProductNum(mainGood.getProduct()));
            }
        });

        //已支付未发货订单
        MainOrder forDeliveryOrder = newRandomOrderFor(order, order);
        makeOrderPay(forDeliveryOrder);
        forDeliveryOrder = mainOrderService.getOrder(forDeliveryOrder.getId());
        assertEquals(forDeliveryOrder.getOrderStatus(), OrderStatus.forDeliver);
        MainOrder finalForDeliveryOrder = forDeliveryOrder;
        saleGoodList.forEach(mainGood -> {
            Optional<MainGood> amountMainGood = finalForDeliveryOrder.getAmounts().keySet().stream().filter(p -> p.getId().equals(mainGood.getId())).findAny();
            if (amountMainGood.isPresent()) {
                int exceptLockStock = (lockStockMap.getOrDefault(mainGood.getId(), 0))
                        + finalForDeliveryOrder.getAmounts().get(amountMainGood.get());
                assertEquals(exceptLockStock, mainOrderService.sumProductNum(mainGood.getProduct()));
                lockStockMap.put(mainGood.getId(), exceptLockStock);
            }
        });
        //已完成订单,不计算在冻结库存里
        MainOrder doneOrder = newRandomOrderFor(order, order);
        makeOrderPay(doneOrder);
        makeOrderDone(doneOrder);
        doneOrder = mainOrderService.getOrder(doneOrder.getId());
        assertEquals(OrderStatus.afterSale, doneOrder.getOrderStatus());
        saleGoodList.forEach(mainGood -> {
            if (lockStockMap.containsKey(mainGood.getId())) {
                assertEquals((int) lockStockMap.get(mainGood.getId()), mainOrderService.sumProductNum(mainGood.getProduct()));
            } else {
                assertEquals(0, mainOrderService.sumProductNum(mainGood.getProduct()));
            }
        });

    }

    @Test
    public void closeOrderTest(){
        //1.先不设定关闭时间
        systemStringService.delete("market.core.service.order.maxMinuteForPay");
        MainOrder orderWithoutClose = newRandomOrderFor(testLogin, testLogin);
        assertEquals(OrderStatus.forPay,orderWithoutClose.getOrderStatus());
        //等他2s,意思意思
        orderWithoutClose = mainOrderService.getOrder(orderWithoutClose.getId());
        waitSometime(2);
        assertEquals(OrderStatus.forPay,orderWithoutClose.getOrderStatus());

        //------------------
        //2.设置关闭时间为10s
        systemStringService.updateSystemString("market.core.service.order.maxMinuteForPay",10);
        MainOrder orderWithClose = newRandomOrderFor(testLogin,testLogin);
        assertEquals(OrderStatus.forPay,orderWithClose.getOrderStatus());
        //等他15s，订单应该关闭了
        waitSometime(15);
        orderWithClose = mainOrderService.getOrder(orderWithClose.getId());
        assertEquals(OrderStatus.close,orderWithClose.getOrderStatus());

    }

    private void waitSometime(long second){
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException ignored) {
        }
    }

}