package cn.lmjia.market.core.entity.support;

/**
 * 订单状态
 *
 * @author CJ
 */
public enum OrderStatus {
    EMPTY,
    /**
     * 等待支付
     */
    forPay,
    /**
     * 等待发货
     */
    forDeliver,
    /**
     * 等待安装
     */
    forInstall,
    /**
     * 订单完成，进入售后状态
     */
    afterSale
}
