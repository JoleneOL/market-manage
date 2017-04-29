package cn.lmjia.market.core.entity.support;

/**
 * 管理员级别
 *
 * @author CJ
 */
public enum ManageLevel {
    root("ROOT");

    private final String[] roles;

    ManageLevel(String... roles) {
        this.roles = roles;
    }

    /**
     * @return role names
     */
    public String[] roles() {
        return roles;
    }
}
