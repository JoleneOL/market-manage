package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.StockInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 包含一大堆库存信息信息
 *
 * @author CJ
 */
public class StockInfoSet {

    private final Set<StockInfo> infoSet = new HashSet<>();

    /**
     * 添加库存信息
     *
     * @param depot
     * @param product
     * @param amount
     */
    public void add(Depot depot, Product product, int amount) {
        StockInfo info = infoSet.stream().filter(stockInfo
                -> stockInfo.getProduct().equals(product) && stockInfo.getDepot().equals(depot))
                .findAny().orElse(null);
        if (info == null) {
            info = new StockInfo(depot, product, amount);
            infoSet.add(info);
        } else
            info.setAmount(info.getAmount() + amount);
    }

    /**
     * 初始数据
     *
     * @param collection
     */
    public void initAll(Collection<StockInfo> collection) {
        infoSet.addAll(collection);
    }

    /**
     * 初始为0
     *
     * @param depotCollection
     * @param productCollection
     */
    public void initAll(Collection<Depot> depotCollection, Collection<Product> productCollection) {
        for (Depot depot : depotCollection) {
            for (Product product : productCollection)
                add(depot, product, 0);
        }
    }

    public Set<StockInfo> forDepot(Depot depot) {
        return infoSet.stream()
                .filter(stockInfo -> stockInfo.getDepot().equals(depot))
                .collect(Collectors.toSet());
    }

    public Set<StockInfo> forProduct(Product product) {
        return infoSet.stream()
                .filter(stockInfo -> stockInfo.getProduct().equals(product))
                .collect(Collectors.toSet());
    }

    public Set<StockInfo> forAll() {
        return infoSet.stream().collect(Collectors.toSet());
    }

    public int forOne(Product product, Depot depot) {
        StockInfo info = infoSet.stream().filter(stockInfo
                -> stockInfo.getProduct().equals(product) && stockInfo.getDepot().equals(depot))
                .findAny().orElse(null);
        if (info == null)
            return 0;
        return info.getAmount();
    }


}
