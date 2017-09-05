package cn.lmjia.market.core.exception;

import me.jiangcai.logistics.entity.Product;

/**
 * 没必要的物流，可能是某货品已满足订单需要
 *
 * @author CJ
 */
public class UnnecessaryShipException extends Exception {

    private final Product product;

    public UnnecessaryShipException(Product product) {
        this.product = product;
    }

    @Override
    public String getMessage() {
        return product.getName() + "无需再发了。";
    }
}
