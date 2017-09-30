package cn.lmjia.market.core.entity.settlement;

/**
 * @author CJ
 */
public enum CommissionJournalType {

    mainOrderCommission("订单收益"),
    withdraw("成功提现"),;
    private final String message;

    CommissionJournalType(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
