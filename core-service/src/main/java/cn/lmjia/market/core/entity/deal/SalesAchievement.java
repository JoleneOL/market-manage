package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 销售人员业绩，每次推广成交
 * 推广时间，成交，成交时间，相关订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class SalesAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 是否已被使用
     */
    private boolean picked;
    @ManyToOne
    private Salesman whose;
    /**
     * 下单时的奖励比例
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal currentRate;
    @ManyToOne
    private Login targetLogin;
    /**
     * 建立关系的时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime targetTime;
    /**
     * 由此而带来的订单
     */
    @OneToOne(mappedBy = "salesAchievement")
    private MainOrder mainOrder;
    /**
     * 记录
     */
    @Column(length = 40)
    private String remark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesAchievement)) return false;
        SalesAchievement that = (SalesAchievement) o;
        return Objects.equals(whose, that.whose) &&
                Objects.equals(targetLogin, that.targetLogin) &&
                Objects.equals(targetTime, that.targetTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whose, targetLogin, targetTime);
    }
}
