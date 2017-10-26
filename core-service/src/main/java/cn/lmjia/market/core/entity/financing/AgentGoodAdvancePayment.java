package cn.lmjia.market.core.entity.financing;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理商预付货款
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
public class AgentGoodAdvancePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Login login;

    @Column(length = 100)
    private String comment;

    @Column(scale = 2, precision = 20)
    private BigDecimal amount;

    /**
     * 具体的操作者，可控
     */
    @ManyToOne
    private Manager operator;
    /**
     * 财务审批者
     */
    @ManyToOne
    private Manager approval;
    /**
     * 是否被批准，
     * null: 未被处理
     * true: 批准
     * false: 拒绝
     */
    private Boolean approved;
    /**
     * 批准处理时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime approvalTime;

    /**
     * 发生时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime happenTime;
    /**
     * 单据号
     */
    @Column(length = 36)
    private String serial;


    /**
     * @param paymentPath     到货款的路径
     * @param criteriaBuilder cb
     * @return 是否认可货款支付是成功的
     */
    public static Predicate isSuccessPayment(Path<? extends AgentGoodAdvancePayment> paymentPath
            , CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(paymentPath.get(AgentGoodAdvancePayment_.approved));
    }
}
