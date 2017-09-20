package cn.lmjia.market.core.entity.support;

import cn.lmjia.market.core.entity.Login;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 管理员级别
 *
 * @author CJ
 */
public enum ManageLevel {
    root("超级管理员", "ROOT"),
    /**
     * 管理员, 能查看所有后台数据,但是无法操作.
     */
    manager("管理员",Login.ROlE_LOOK),
    /**
     * 客服经理,  订单管理,经销商管理(代理商升级处理)，产品中心，供应链管理.
     */
    customerManager("客服经理",Login.ROLE_ALL_ORDER,Login.ROLE_AllAgent,Login.ROLE_PRODUCT_CENTER,Login.ROLE_SUPPLY_CHAIN),
    /**
     * 财务, 代理商佣金管理
     */
    finance("财务", Login.ROLE_FINANCE),
    //agentManager("代理商管理员", Login.ROLE_AllAgent),
    //promotion("升级专员", Login.ROLE_PROMOTION),
    /**
     * 运营,产品中心以及商城的后台管理 商城后台目前没有!
     */
    operation("运营",Login.ROLE_PRODUCT_CENTER),
    /**
     * 客服,订单管理，物流管理，查看经销商
     */
    customerService("客服", Login.ROLE_ALL_ORDER,Login.ROLE_LOGISTICS);

    private final String[] roles;
    private final String title;

    ManageLevel(String title, String... roles) {
        this.title = title;
        this.roles = roles;
    }

    public static String roleNameToRole(String role) {
        String role2 = role.toUpperCase(Locale.CHINA);
        if (role2.startsWith("ROLE_"))
            return role2;
        return "ROLE_" + role2;
    }

    /**
     * @return role names
     */
    public String[] roles() {
        return roles;
    }

    public Collection<? extends GrantedAuthority> authorities() {
        return Stream.of(roles)
                .map(ManageLevel::roleNameToRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public String title() {
        return title;
    }
}
