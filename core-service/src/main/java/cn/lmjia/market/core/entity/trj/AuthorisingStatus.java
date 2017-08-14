package cn.lmjia.market.core.entity.trj;

/**
 * @author CJ
 */
public enum AuthorisingStatus {
    Unused("未使用"),
    forOrderComplete("待订单完成"),
    forAuditing("待信审"),
    auditing("信审中"),
    auditingRefuse("信审被拒"),
    forSettle("待结算"),
    settle("已结算");

    private final String message;

    AuthorisingStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
