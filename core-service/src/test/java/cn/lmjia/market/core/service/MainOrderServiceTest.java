package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLimitStockException;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.repository.MainProductRepository;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-08-21.
 */
public class MainOrderServiceTest extends CoreServiceTest {
    private static final Log log = LogFactory.getLog(MainOrderServiceTest.class);
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private MainProductRepository mainProductRepository;
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
            lockStockMap.put(mainGood.getId(), lockStock);
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
    public void closeOrderTest() {
        //------------------
        //1.设置关闭时间为10s
        systemStringService.updateSystemString("market.core.service.order.maxMinuteForPay", 10);
        MainOrder orderWithClose = newRandomOrderFor(testLogin, testLogin);
        assertEquals(OrderStatus.forPay, orderWithClose.getOrderStatus());
        //等他15s，订单应该关闭了
        waitSometime(15);
        orderWithClose = mainOrderService.getOrder(orderWithClose.getId());
        assertEquals(OrderStatus.close, orderWithClose.getOrderStatus());

        //2.先不设定关闭时间，然后再开启关闭订单功能
        systemStringService.delete("market.core.service.order.maxMinuteForPay");
        MainOrder orderWithoutClose = newRandomOrderFor(testLogin, testLogin);
        assertEquals(OrderStatus.forPay, orderWithoutClose.getOrderStatus());
        //等他2s,意思意思
        orderWithoutClose = mainOrderService.getOrder(orderWithoutClose.getId());
        waitSometime(2);
        assertEquals(OrderStatus.forPay, orderWithoutClose.getOrderStatus());
        systemStringService.updateSystemString("market.core.service.order.maxMinuteForPay", 10);
        mainOrderService.createExecutorToForPayOrder();
        //之前已经等了2s了，现在等9s 就够了
        waitSometime(9);
        orderWithoutClose = mainOrderService.getOrder(orderWithoutClose.getId());
        assertEquals(OrderStatus.close, orderWithoutClose.getOrderStatus());
    }

    @Test
    public void checkOrderStockWithExceptionTest() {
        systemStringService.updateSystemString("market.core.service.product.offsetHour", 0);
        mainGoodService.forSale().forEach(p->{
            mainOrderService.cleanProductStock(p.getProduct());
        });
        List<MainGood> saleGoodList = mainGoodService.forSale();
        MainGood orderGood = saleGoodList.get(0);
        mainOrderService.cleanProductStock(orderGood.getProduct());
        //对这个订单下单超过货品库存
        Map<MainGood, Integer> amounts = new HashMap<>();
        amounts.put(orderGood, orderGood.getProduct().getStock() + random.nextInt(10));
        MainOrder mainOrder = null;
        try {
            newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (Exception e) {
            assertTrue(e instanceof MainGoodLowStockException);
        }

        //对货品设置一个预计售罄时间，货品剩几件就加N-1天，这样今天理论上只能下一个数量为1的订单
        LocalDate planSellOutDate = LocalDate.now().plusDays(orderGood.getProduct().getStock() - 1);
        orderGood.getProduct().setPlanSellOutDate(planSellOutDate);
        mainProductRepository.save(orderGood.getProduct());
        mainOrderService.cleanProductStock(orderGood.getProduct());
        amounts.clear();
        amounts.put(orderGood, 2);
        try {
            newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (Exception e) {
            assertTrue(e instanceof MainGoodLimitStockException);
            log.debug(((MainGoodLimitStockException)e).toData());
        }
        //如果数量是1，是能下单成功的
        amounts.clear();
        amounts.put(orderGood, 1);
        try {
            mainOrder = newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (MainGoodLowStockException ignored) {
        }
        assertNotNull(mainOrder);
        //这个时候 上面的订单是未发货状态，但是还是冻结着的，所以还不能下单
        try {
            newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (MainGoodLowStockException e) {
            assertTrue(e instanceof MainGoodLimitStockException);
        }
        //再设置预计售罄时间为明天，这样今天应该还能下N/2个单，N是指货品的初始库存数
        planSellOutDate = LocalDate.now().plusDays(1);
        orderGood.getProduct().setPlanSellOutDate(planSellOutDate);
        mainProductRepository.save(orderGood.getProduct());
        //还没生效，因为map还没清空
        try {
            newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (MainGoodLowStockException e) {
            assertTrue(e instanceof MainGoodLimitStockException);
        }
        mainOrderService.cleanProductStock(orderGood.getProduct());
        //试一试N/2+1的订单应该是限购的
        amounts.clear();
        amounts.put(orderGood, orderGood.getProduct().getStock() / 2 + 1);
        try {
            newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (MainGoodLowStockException e) {
            assertTrue(e instanceof MainGoodLimitStockException);
        }
        //N/2就能下单成功了
        amounts.clear();
        amounts.put(orderGood, orderGood.getProduct().getStock() / 2);
        try {
            mainOrder = newRandomOrderFor(testLogin, testLogin, randomMobile(), amounts);
        } catch (MainGoodLowStockException ignored) {
        }
        assertNotNull(mainOrder);

    }

    @Test
    public void testSumStock() throws MainGoodLowStockException {
        //先看看初始时候的可用库存
        List<MainGood> saleGoodList = mainGoodService.forSale();
        MainGood orderGood = saleGoodList.get(0);
        //下个单
        Map<MainGood, Integer> amounts = new HashMap<>();
        amounts.put(orderGood, random.nextInt(10));
        MainOrder order = newRandomOrderFor(testLogin,testLogin,randomMobile(),amounts);
        assertNotNull(order);
        //再次获取商品的可用库存
        List<MainGood> afterSaleGoodList = mainGoodService.forSale();
        MainGood afterOrderGood = afterSaleGoodList.get(0);
        assertEquals(orderGood.getProduct().getStock() - amounts.get(orderGood) , afterOrderGood.getProduct().getStock());

    }

    /**
     * 只是一个多线程的多重锁检查，没有做校验
     */
    @Test
    @Ignore
    public void newOrderLockTest() {
//        List<MainGood> saleGoodList = mainGoodService.forSale();
//        MainGood orderGood = saleGoodList.get(0);
//        Map<MainGood, Integer> amounts = new HashMap<>();
//        amounts.put(orderGood, orderGood.getProduct().getStock() + random.nextInt(10));
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        int threadNum = 3;
        log.info("-------------");
        while (threadNum-- > 0) {
            //本次任务完成后才会执行新的任务
            executor.scheduleWithFixedDelay(() -> {
                int i = 5;
                while (i-- > 0) {
                    log.debug("thread:" + Thread.currentThread().getName());
                    newRandomOrderFor(testLogin, testLogin);
                }
            }, 0, 5, TimeUnit.SECONDS);

        }
        waitSometime(5);
    }

    private void waitSometime(long second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException ignored) {
        }
    }

}