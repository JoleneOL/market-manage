package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
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

    @ManyToOne(optional = false)
    private OrderCommission orderCommission;

    /**
     * 相关比例
     * 必然是一个正数
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal rate;

    /**
     * 金额，可以是负数
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;

    /**
     * @param commissionFrom  佣金from
     * @param criteriaBuilder cb
     * @return 该佣金是否真实可用的
     */
    public static Predicate Reality(From<?, Commission> commissionFrom, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isFalse(commissionFrom.get("orderCommission").get("pending"));
    }
}
