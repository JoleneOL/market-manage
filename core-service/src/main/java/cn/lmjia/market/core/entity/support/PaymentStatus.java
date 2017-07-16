package cn.lmjia.market.core.entity.support;

/**
 * 支付状态
 *
 * @author CJ
 */
public enum PaymentStatus {
    wait("未支付"),
    payed("已支付"),
    partialPayed("部分支付"),
    refund("已退款"),
    partialRefund("部分退款");

    private final String message;

    PaymentStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
