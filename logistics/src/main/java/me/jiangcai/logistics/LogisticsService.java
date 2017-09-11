package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

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
     */
    @Transactional
    default StockShiftUnit makeShift(LogisticsSupplier supplier, DeliverableOrder order, Collection<Thing> things
            , LogisticsSource source, LogisticsDestination destination) {
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
     */
    @Transactional
    default StockShiftUnit makeShiftForNormal(LogisticsSupplier supplier, DeliverableOrder order, Product product
            , int amount
            , LogisticsSource source, LogisticsDestination destination) {
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
     */
    @Transactional
    default StockShiftUnit makeShiftForNormal(LogisticsSupplier supplier, DeliverableOrder order, Product product
            , int amount
            , LogisticsSource source, LogisticsDestination destination, int options) {
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
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param order       可选的宿主订单
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @param options     选项;{@link LogisticsOptions}
     * @return 配送
     */
    @Transactional
    StockShiftUnit makeShift(LogisticsSupplier supplier, DeliverableOrder order, Collection<Thing> things
            , LogisticsSource source, LogisticsDestination destination, int options);

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
}
