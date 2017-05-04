package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

/**
 * 代理体系的成员，具备上下级关系
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AgentLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 它都一个登录与之对应，但是可能会有一个登录对应多个代理体系成员
     */
    @ManyToOne
    private Login login;
    /**
     * 等级说明
     */
    @Column(length = 20)
    private String rank;
    /**
     * 上级;作为最顶级的代理商它的superior是null
     */
    @ManyToOne
    private AgentLevel superior;
    @OneToMany(mappedBy = "superior")
    private List<AgentLevel> subAgents;

    @Override
    public String toString() {
        return "AgentLevel{" +
                "id=" + id +
                ", login=" + login +
                ", rank='" + rank + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentLevel)) return false;
        AgentLevel that = (AgentLevel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
