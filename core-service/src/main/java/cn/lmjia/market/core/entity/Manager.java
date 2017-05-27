package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.support.ManageLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 区别于一般的登录者，这些登录者具备管理职能
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Manager extends Login {

    @Enumerated(EnumType.STRING)
    private ManageLevel level;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        String[]
        final String[] roles = level.roles();
        String[] newRoles = Arrays.copyOf(roles, roles.length + 1);
        newRoles[newRoles.length - 1] = "ROLE_" + ROLE_MANAGER;
        return Stream.of(newRoles)
                .map(role -> {
                    role = role.toUpperCase(Locale.CHINA);
                    if (role.startsWith("ROLE_"))
                        return role;
                    return "ROLE_" + role;
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isManageable() {
        return true;
    }

    @Override
    public String getLoginTitle() {
        return level.title();
    }
}
