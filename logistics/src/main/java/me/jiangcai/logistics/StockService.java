package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.UsageStock;
import me.jiangcai.logistics.event.ShiftEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

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

    /**
     * @return 所有库存信息
     */
    @Transactional(readOnly = true)
    StockInfoSet usableStock();

    /**
     * @param productSpec 可选的产品规格
     * @param depotSpec   可选的仓库规格
     * @return 特定条件的库存信息
     */
    @Transactional(readOnly = true)
    StockInfoSet usableStockInfo(BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpec
            , BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpec);

    /**
     * @return 所有上架库存信息
     */
    @Transactional(readOnly = true)
    StockInfoSet enabledUsableStock();

    /**
     * 如果给定了条件，则不再检查是否是上架状态的
     *
     * @param productSpec 可选的产品规格
     * @param depotSpec   可选的仓库规格
     * @return 特定条件的上架库存信息
     */
    @Transactional(readOnly = true)
    StockInfoSet enabledUsableStockInfo(BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpec
            , BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpec);
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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    void shiftEventUp(ShiftEvent event);

    /**
     * @param product 货品
     * @return 特定货品可用库存总量
     */
    @Transactional(readOnly = true)
    int usableStockTotal(Product product);

    /**
     * @param condition 相关条件
     * @return 符合条件的仓库
     */
    @Transactional(readOnly = true)
    List<Depot> usableDepotFor(BiFunction<CriteriaBuilder, Root<UsageStock>, Predicate> condition);
}
