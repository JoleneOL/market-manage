package cn.lmjia.market.core.entity.withdraw;


import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Setter
@Getter
public class Withdraw {

    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 收款人
     */
    private Login payee;

    /**
     * 收款账号
     */
    private String account;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 收款人电话
     */
    private String mobile;

    /**
     * 提现金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal withdraw;

    private Invoice invoice;

}
