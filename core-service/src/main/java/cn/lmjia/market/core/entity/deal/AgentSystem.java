package cn.lmjia.market.core.entity.deal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;

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
    @ElementCollection
    private Map<Integer, AgentRate> rates;
    /**
     * 下单人的直接奖励
     */
    private AgentRate orderRate;
}
