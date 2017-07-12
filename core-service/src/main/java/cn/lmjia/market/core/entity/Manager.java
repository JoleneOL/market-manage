package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.support.ManageLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import java.util.Set;
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

    @Column(length = 10)
    private String department;
    /**
     * 真实姓名
     */
    @Column(length = 10)
    private String realName;
    /**
     * 备注
     */
    @Column(length = 200)
    private String comment;
    @Enumerated(EnumType.STRING)
    private ManageLevel level;
    /**
     * 新的等级设置
     */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<ManageLevel> levelSet;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 固定的权限
        Stream<String> fixed = Stream.of("ROLE_" + ROLE_MANAGER);
        if (level != null) {
            fixed = Stream.concat(fixed, Stream.of(level.roles()));
        }
        if (levelSet != null) {
            fixed = Stream.concat(fixed, levelSet.stream()
                    .flatMap(level1 -> Stream.of(level1.roles())));
        }

        return fixed
                .map(ManageLevel::roleNameToRole)
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
