package cn.lmjia.market.core.entity.cache;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * 2个身份的关系缓存!
 * 对于代理商的改变（升级，增加，删除）都应该重建缓存
 * 对于客户的增加则应该直接添加缓存（因为该操作足够频繁）
 * to是from的下线，而level则描述to的级别
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class LoginRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 处于哪个代理系统
     */
    @ManyToOne(optional = false)
    private AgentSystem system;
    /**
     * 关系的上级
     */
    @ManyToOne(optional = false)
    private Login from;
    /**
     * 关系的下级
     */
    @ManyToOne(optional = false)
    private Login to;
    /**
     * 下级的等级
     */
    private int level;

    @Override
    public String toString() {
        return "LoginRelation{" +
                "from=" + from.getId() +
                ", to=" + to.getId() +
                ", level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRelation)) return false;
        LoginRelation relation = (LoginRelation) o;
        return level == relation.level &&
                Objects.equals(system, relation.system) &&
                Objects.equals(from, relation.from) &&
                Objects.equals(to, relation.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, from, to, level);
    }
}
