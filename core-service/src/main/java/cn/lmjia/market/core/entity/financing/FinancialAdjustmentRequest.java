package cn.lmjia.market.core.entity.financing;

import cn.lmjia.market.core.entity.Manager;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务调整申请
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class FinancialAdjustmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 金额，单位元
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;
    /**
     * 申请时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime requestTime;
    /**
     * 申请
     */
    @ManyToOne
    private Manager requestBy;
    /**
     * 许可
     */
    private boolean approve;

}
