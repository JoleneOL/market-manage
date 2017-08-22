package cn.lmjia.market.core.entity.support;

/**
 * 提现状态
 */
public enum WithdrawStatus {
    EMPTY("E"),
    init("初始化"),
    /**
     * 待审核
     */
    checkPending("待审核"),
    refuse("已拒绝"),
    /**
     * 提现成功
     */
    success("已成功");

    private final String message;

    WithdrawStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
