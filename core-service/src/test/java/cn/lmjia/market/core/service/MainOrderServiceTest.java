package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.entity.support.OrderStatus;
import org.junit.Before;
import org.junit.Ignore;
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
        //没有订单，冻结库存应该为0
        saleGoodList.forEach(mainGood -> assertEquals(0, mainOrderService.lockedStock(mainGood.getProduct())));

        //未支付订单
        MainOrder forPayOrder = newRandomOrderFor(order, order);
        assertEquals(OrderStatus.forPay, forPayOrder.getOrderStatus());
        saleGoodList.forEach(mainGood -> {
            Optional<MainGood> amountMainGood = forPayOrder.getAmounts().keySet().stream().filter(p -> p.getId().equals(mainGood.getId())).findAny();
            if (amountMainGood.isPresent()) {
                int exceptLockStock = forPayOrder.getAmounts().get(amountMainGood.get());
                assertEquals(exceptLockStock, mainOrderService.lockedStock(mainGood.getProduct()));
                lockStockMap.put(mainGood.getId(), exceptLockStock);
            } else {
                assertEquals(0, mainOrderService.lockedStock(mainGood.getProduct()));
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
                assertEquals(exceptLockStock, mainOrderService.lockedStock(mainGood.getProduct()));
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
                assertEquals((int) lockStockMap.get(mainGood.getId()), mainOrderService.lockedStock(mainGood.getProduct()));
            } else {
                assertEquals(0, mainOrderService.lockedStock(mainGood.getProduct()));
            }
        });

    }

}