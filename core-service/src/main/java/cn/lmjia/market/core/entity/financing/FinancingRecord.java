package cn.lmjia.market.core.entity.financing;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Setter
@Getter
public abstract class FinancingRecord {
    /**
     * 利益相关方
     */
    @ManyToOne
    private Login who;
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
    // 应该还有类型 比如线上支付，银行转账，现金交易
    private Integer type;
    /**
     * 金额，单位元
     * 收入为正，开支则为负
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;
    /**
     * 财务审核状态
     */
    private boolean checked;
    /**
     * 哪儿财务审核的
     */
    @ManyToOne
    private Manager validateBy;
    /**
     * 审核时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime validateTime;

    /**
     * 调整订单
     */
    @ManyToOne
    private FinancialAdjustmentRequest adjustmentRequest;
}
