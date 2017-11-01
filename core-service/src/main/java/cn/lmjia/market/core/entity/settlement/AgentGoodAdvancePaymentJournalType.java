package cn.lmjia.market.core.entity.settlement;

/**
 * @author CJ
 */
public enum AgentGoodAdvancePaymentJournalType {

    makeOrder("批货"),
    payment("预付"),;
    private final String message;

    AgentGoodAdvancePaymentJournalType(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
