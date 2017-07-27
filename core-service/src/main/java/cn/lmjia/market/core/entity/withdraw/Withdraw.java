package cn.lmjia.market.core.entity.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 提现
 */
@Entity
@Setter
@Getter
public class Withdraw {

    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流水号
     */
    private int serialId;
    /**
     * 收款人
     */
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REFRESH})
    private Login payee;

    /**
     * 收款账号
     */
    @Column(length = 20)
    private String account;

    /**
     * 开户行
     */
    @Column(length = 50)
    private String bank;

    /**
     * 收款人电话
     */
    @Column(length = 20)
    private String mobile;

    /**
     * 提现金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal withdrawMoney;

    /**
     * 提现时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime withdrawTime;

    /**
     * 提现的发票信息
     */
    @OneToOne(cascade={CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REFRESH})
    private Invoice invoice;

    /**
     * 提现的状态
     */
    private WithdrawStatus withdrawStatus;

    /**
     * 备注信息
     */
    @Column(length = 256)
    private String remark;
}
