package cn.lmjia.market.core.entity.support;

/**
 * 订单状态
 *
 * @author CJ
 */
public enum OrderStatus {
    EMPTY("E"),
    /**
     * 等待支付
     */
    forPay("待付款"),
    /**
     * 等待发货
     */
    forDeliver("待发货"),
    /**
     * 等待发货
     */
    forDeliverConfirm("待收货"),
    /**
     * 等待安装
     */
    forInstall("未安装"),
    /**
     * 订单完成，进入售后状态
     */
    afterSale("已安装");

    private final String message;

    OrderStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
