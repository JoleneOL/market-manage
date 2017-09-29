package cn.lmjia.market.core.entity.settlement;

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
 * 用户的佣金流水记录
 * 此处使用视图设计
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Table(name = "LoginCommissionJournal")
@Entity
@Getter
@Setter
public class LoginCommissionJournal {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "LOGIN_ID")
    private Login login;
    /**
     * 如果是主订单所带来的佣金收益则有值
     */
    @Column(name = "MAIN_ORDER_ID")
    private Long mainOrderId;
    @Column(name = "HAPPEN_TIME")
    private LocalDateTime happenTime;
    @Column(name = "MESSAGE")
    private String message;
    /**
     * 变化额，正数表示增加，负数表示减少
     */
    @Column(name = "CHANGED")
    private BigDecimal changed;

}
