package cn.lmjia.market.dealer.entity;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * 用户从OrderCommission中获取的佣金收益
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 谁获得的佣金
     */
    @ManyToOne(optional = false)
    private Login who;
    /**
     * 那个代理商获得的佣金
     * 如果是客户直推这个值是存在空的可能的
     */
    @ManyToOne
    private AgentLevel agent;

    @ManyToOne
    private OrderCommission orderCommission;

    /**
     * 金额，可以是负数
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;

}
