package cn.lmjia.market.core.entity.settlement;

import cn.lmjia.market.core.define.Journal;
import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理商货款记录流水
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Table(name = "AgentGoodAdvancePaymentJournal")
@Entity
@Getter
@Setter
public class AgentGoodAdvancePaymentJournal implements Journal {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "LOGIN_ID")
    private Login login;
    /**
     * 如果是主订单所带来的佣金收益则有值
     */
    @Column(name = "AGENT_PREPAYMENT_ORDER_ID")
    private Long agentPrepaymentOrderId;
    @Column(name = "HAPPEN_TIME")
    private LocalDateTime happenTime;
    @Column(name = "TYPE")
    private AgentGoodAdvancePaymentJournalType type;
    /**
     * 变化额，正数表示增加，负数表示减少
     */
    @Column(name = "CHANGED")
    private BigDecimal changed;

    @Override
    public Long getMainOrderId() {
        return null;
    }

    public Money getChangedAbsMoney() {
        return new Money(getChanged().abs());
    }
}
