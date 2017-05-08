package cn.lmjia.market.core.entity.support;

import cn.lmjia.market.core.entity.Login;

/**
 * 管理员级别
 *
 * @author CJ
 */
public enum ManageLevel {
    root("超级管理员", "ROOT"),
    agentManager("代理管理员", Login.ROLE_AllAgent);

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
