package cn.lmjia.market.core.entity.support;

/**
 * 管理员级别
 *
 * @author CJ
 */
public enum ManageLevel {
    root("超级管理员", "ROOT");

    private final String[] roles;
    private final String title;

    ManageLevel(String title, String... roles) {
        this.title = title;
        this.roles = roles;
    }

    /**
     * @return role names
     */
    public String[] roles() {
        return roles;
    }

    public String title() {
        return title;
    }
}
