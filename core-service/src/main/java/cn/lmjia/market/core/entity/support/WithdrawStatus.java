package cn.lmjia.market.core.entity.support;

/**
 * 提现状态
 */
public enum WithdrawStatus {
    EMPTY("E"),
    init("待验证"),
    /**
     * 待审核
     */
    checkPending("审核中"),
    refuse("失败"),
    /**
     * 提现成功
     */
    success("成功到账");

    private final String message;

    WithdrawStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public boolean isSuccess() {
        return this == success;
    }

    public boolean isFailed() {
        return this == refuse;
    }
}
