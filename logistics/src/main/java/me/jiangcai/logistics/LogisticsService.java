package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author CJ
 */
public interface LogisticsService {

    /**
     * 尝试模拟一个状态的变化
     *
     * @param unitId 物流订单pk
     * @param status 改变至状态
     */
    @Transactional
    void mockToStatus(long unitId, ShiftStatus status);
    //Distribution resource planning

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param order       可选的宿主订单
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     * @throws UnnecessaryShipException 没必要的物流
     */
    @Transactional
    default StockShiftUnit makeShift(LogisticsSupplier supplier, DeliverableOrder order, Collection<Thing> things
            , LogisticsSource source, LogisticsDestination destination) throws UnnecessaryShipException {
        return makeShift(supplier, order, things, source, destination, 0);
    }

    /**
     * 开启配送一种普通状态的商品
     *
     * @param supplier    物流供应商
     * @param order       可选的宿主订单
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @param product     货品
     * @param amount      数量
     * @return 配送
     * @throws UnnecessaryShipException 没必要的物流
     */
    @Transactional
    default StockShiftUnit makeShiftForNormal(LogisticsSupplier supplier, DeliverableOrder order, Product product
            , int amount
            , LogisticsSource source, LogisticsDestination destination) throws UnnecessaryShipException {
        return makeShiftForNormal(supplier, order, product, amount, source, destination, 0);
    }

    /**
     * 开启配送一种普通状态的商品
     *
     * @param supplier    物流供应商
     * @param order       可选的宿主订单
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @param product     货品
     * @param amount      数量
     * @param options     选项;{@link LogisticsOptions}
     * @return 配送
     * @throws UnnecessaryShipException 没必要的物流
     */
    @Transactional
    default StockShiftUnit makeShiftForNormal(LogisticsSupplier supplier, DeliverableOrder order, Product product
            , int amount
            , LogisticsSource source, LogisticsDestination destination, int options) throws UnnecessaryShipException {
        return makeShift(supplier, order, Collections.singleton(new Thing() {
            @Override
            public Product getProduct() {
                return product;
            }

            @Override
            public ProductStatus getProductStatus() {
                return ProductStatus.normal;
            }

            @Override
            public int getAmount() {
                return amount;
            }
        }), source, destination, options);
    }

    /**
     * @param order 订单
     * @return 这个订单相关的库存信息
     */
    @Transactional(readOnly = true)
    Map<Depot, Map<Product, Integer>> getDepotInfo(DeliverableOrder order);

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param order       可选的宿主订单
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @param options     选项;{@link LogisticsOptions}
     * @return 配送
     * @throws UnnecessaryShipException 没必要的物流
     */
    @Transactional
    StockShiftUnit makeShift(LogisticsSupplier supplier, DeliverableOrder order, Collection<Thing> things
            , LogisticsSource source, LogisticsDestination destination, int options) throws UnnecessaryShipException;

    /**
     * 模拟生成一个安装时间
     *
     * @param unitId 物流id
     */
    @Transactional
    void mockInstallationEvent(long unitId);

    @EventListener(ShiftEvent.class)
    @Transactional
    @Order(Ordered.LOWEST_PRECEDENCE)
    void forShiftEvent(ShiftEvent event);

    @EventListener(InstallationEvent.class)
    @Transactional
    @Order(Ordered.LOWEST_PRECEDENCE)
    void forInstallationEvent(InstallationEvent event);

    /**
     * 通过渲染model意图展示发货时所需的数据，可获得数据
     * <ul>
     * <li>currentData order</li>
     * <li>requiredList 发货所需货品列表</li>
     * <li>depotInfo 相关库存信息，应该是一个Map key为{@link me.jiangcai.logistics.entity.Depot}而value 为一个Map,{@link Product}和数量</li>
     * <li>orderPK {@link me.jiangcai.logistics.model.DeliverableOrderId},它的toString经过Spring MVC也会还原成一致的实例</li>
     * </ul>
     *
     * @param order 需发货的订单
     * @param model 渲染目标model
     */
    @Transactional(readOnly = true)
    void viewModelForDelivery(DeliverableOrder order, Model model);
}
