package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.data.jpa.domain.Specification;

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

    /**
     * @param login         当前身份
     * @param specification 既定规则
     * @return 属于当前身份所有佣金记录的规格
     */
    public static Specification<Commission> listAllSpecification(Login login, Specification<Commission> specification) {
        return new AndSpecification<>((root, query, cb) -> {
            query = query.groupBy(root.get("orderCommission"));
            return cb.and(
                    cb.equal(root.get("who"), login)
                    , cb.isFalse(root.get("orderCommission").get("source").get("disableSettlement"))
                    , cb.notEqual((root.get("amount")), BigDecimal.ZERO)
//                        , Commission.Reality(root, cb)
            );
        }, specification);
    }

    /**
     * @param login         当前身份
     * @param specification 既定规则
     * @return 属于当前身份真实可用佣金记录的规格
     */
    public static Specification<Commission> listRealitySpecification(Login login, Specification<Commission> specification) {
        return new AndSpecification<>((root, query, cb) -> {
            query = query.groupBy(root.get("orderCommission"));
            return cb.and(
                    cb.equal(root.get("who"), login)
                    , cb.isFalse(root.get("orderCommission").get("source").get("disableSettlement"))
                    , cb.notEqual((root.get("amount")), BigDecimal.ZERO)
                    , Commission.Reality(root, cb)
            );
        }, specification);
    }
}
