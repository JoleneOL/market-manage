package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.ProductStatus;

/**
 * 物件，物流所管理和运输的最小单位
 *
 * @author CJ
 */
public interface Thing {
    /**
     * @return 编号
     */
    String getProductCode();

    /**
     * @return 物品名字
     */
    String getProductName();

    Product getProduct();

    ProductStatus getProductStatus();

    /**
     * @return 数量
     */
    int getAmount();
}
