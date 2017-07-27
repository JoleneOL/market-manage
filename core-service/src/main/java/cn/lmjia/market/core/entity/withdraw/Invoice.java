package cn.lmjia.market.core.entity.withdraw;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 发票信息
 */
@Entity
@Setter
@Getter
public class Invoice {

    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发票的公司名称
     */
    @Column(length = 50)
    private String companyName;

    /**
     * 发票的税号
     */
    @Column(length = 50)
    private  String taxnumber;

    /**
     * 物流单号
     */
    @Column(length = 50)
    private String logisticsnumber;

    /**
     * 物流公司
     */
    @Column(length = 50)
    private String logisticscompany;

 }
