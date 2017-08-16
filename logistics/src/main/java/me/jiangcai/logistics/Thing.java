package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.ProductStatus;

/**
 * 物件，物流所管理和运输的最小单位
 *
 * @author CJ
 */
public interface Thing {

    Product getProduct();

    ProductStatus getProductStatus();

    /**
     * @return 数量
     */
    int getAmount();
}
