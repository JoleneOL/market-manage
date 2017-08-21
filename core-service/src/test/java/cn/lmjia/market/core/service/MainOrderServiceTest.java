package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-08-21.
 */
public class MainOrderServiceTest extends CoreServiceTest{
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
    @Ignore
    public void lockedStock() throws Exception {
        final Login order = testLogin;
        List<MainGood> saleGoodList = mainGoodService.forSale();
        Map<MainGood,Integer> lockStockMap = new HashMap<>();
        //没有订单，冻结库存应该为0
        saleGoodList.forEach(mainGood -> assertEquals(0,mainOrderService.lockedStock(mainGood.getProduct())));
        //未支付订单
        MainOrder forPayOrder = newRandomOrderFor(order,order);
        assertEquals(forPayOrder.getOrderStatus(), OrderStatus.forPay);
        saleGoodList.forEach(mainGood -> {
            if(forPayOrder.getAmounts().containsKey(mainGood)){
                int exceptLockStock = forPayOrder.getAmounts().get(mainGood);
                assertEquals(exceptLockStock,mainOrderService.lockedStock(mainGood.getProduct()));
                lockStockMap.put(mainGood,exceptLockStock);
            }else{
                assertEquals(0,mainOrderService.lockedStock(mainGood.getProduct()));
            }
        });

        //未发货订单
//        MainOrder forDeliveryOrder = newRandomOrderFor(order,order);
//        makeOrderPay(forDeliveryOrder);
//        assertEquals(forDeliveryOrder.getOrderStatus(), OrderStatus.forDeliver);
        //已完成订单


    }

}