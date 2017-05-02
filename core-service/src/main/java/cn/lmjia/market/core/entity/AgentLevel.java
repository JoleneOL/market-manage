package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.selection.FunctionSelection;
import cn.lmjia.market.core.selection.Selection;
import cn.lmjia.market.core.service.ReadService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Arrays;
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

    public static List<Selection<AgentLevel>> ManageSelections(ReadService readService) {
        return Arrays.asList(
                new FunctionSelection<>("id", AgentLevel::getId)
                , new FunctionSelection<>("rank", AgentLevel::getRank)
                , new FunctionSelection<>("name", level -> readService.nameForPrincipal(level.getLogin()))
                , new FunctionSelection<>("phone", level -> readService.mobileFor(level.getLogin()))
                , new FunctionSelection<AgentLevel>("subordinate", level -> "???")
                // 如何实现循环调用？
                , new Selection<AgentLevel>() {
                    @Override
                    public String getName() {
                        return "children";
                    }

                    @Override
                    public boolean supportIterable() {
                        return true;
                    }

                    @Override
                    public Object selectData(AgentLevel data) {
                        return data.getSubAgents();
                    }
                }
        );
    }

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
