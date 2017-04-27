package cn.lmjia.market.dealer.entity;

import cn.lmjia.market.core.entity.AgentLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 具备销售功能的代理商
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Agent extends AgentLevel {

    /**
     * 隶属于该分代理
     */
    @ManyToOne
    private SubAgent subAgent;

}
