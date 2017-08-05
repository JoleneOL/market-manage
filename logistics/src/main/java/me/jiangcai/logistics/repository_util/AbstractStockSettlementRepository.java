package me.jiangcai.logistics.repository_util;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AbstractStockSettlementRepository<T extends StockSettlement> extends JpaRepository<T, Long>
        , JpaSpecificationExecutor<T> {
    T findTop1ByDepotAndProductOrderByTimeDesc(Depot depot, Product product);
}
