package cn.lmjia.market.core.entity.withdraw;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private String companyName;

    /**
     * 发票的税号
     */
    private  String taxnumber;

    /**
     * 物流单号
     */
    private String logisticsnumber;

    /**
     * 物流公司
     */
    private String logisticscompany;

 }
