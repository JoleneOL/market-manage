package cn.lmjia.market.core.entity.deal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 每一个顶级代理都掌控一个代理系统
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class AgentSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 销售奖励提成
     */
    @ElementCollection
    private Map<Integer, AgentRate> rates;
    /**
     * 销售提成
     */
    private BigDecimal orderRate;

    /**
     * 区域奖励提成
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal addressRate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentSystem)) return false;
        AgentSystem system = (AgentSystem) o;
        return Objects.equals(id, system.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
