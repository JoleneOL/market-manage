package cn.lmjia.market.dealer.entity;

import cn.lmjia.market.core.entity.AgentLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 分代理
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class SubAgent extends AgentLevel {
    /**
     * 隶属于该总代理
     */
    @ManyToOne
    private GeneralAgent generalAgent;
}
