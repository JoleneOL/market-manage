package cn.lmjia.market.core.entity.financing;

import cn.lmjia.market.core.entity.deal.AgentLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 代理费记录
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class AgentFeeRecord extends AgentIncomeRecord {
    @ManyToOne
    private AgentLevel agent;

}
