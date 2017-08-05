package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.ShiftEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author CJ
 */
public interface LogisticsService {

    //Distribution resource planning

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     */
    @Transactional
    StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination);

    /**
     * 开启配送，和安装一体；并非所有物流都支持
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     * @see me.jiangcai.logistics.option.LogisticsOptions#Installation
     */
    @Transactional
    StockShiftUnit makeShiftWithInstallation(LogisticsSupplier supplier, Collection<Thing> things
            , LogisticsSource source
            , LogisticsDestination destination);

    /**
     * @param depot   仓库
     * @param product 货品
     * @return 可用库存
     */
    @Transactional(readOnly = true)
    int usableStock(Depot depot, Product product);

    /**
     * 直接添加库存
     *
     * @param depot   仓库
     * @param product 货品
     * @param amount  数量
     * @param message 可选留言
     */
    @Transactional
    void addStock(Depot depot, Product product, int amount, String message);

    @EventListener(ShiftEvent.class)
    @Transactional
    void shiftEventUp(ShiftEvent event);
}
