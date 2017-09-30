package cn.lmjia.market.core.entity.support;

/**
 * @author CJ
 */
public enum CommissionType {
    directMarketing("直销"),
    marketing("销售"),
    guideMarketing("推荐"),
    regionService("地域"),
    sales("促销"),
    other("其他"),;

    private final String message;

    CommissionType(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
