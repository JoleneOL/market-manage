package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.StockInfo;
import me.jiangcai.logistics.event.ShiftEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 库存服务
 *
 * @author CJ
 */
public interface StockService {

    /**
     * @param depot   仓库
     * @param product 货品
     * @return 可用库存
     */
    @Transactional(readOnly = true)
    int usableStock(Depot depot, Product product);

    List<StockInfo> usableStock();
    // 库存 为0 的信息
    // 库存 小于警戒线的信息
    // 每一种货品的 所有库存信息


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
