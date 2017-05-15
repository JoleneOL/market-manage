package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * 主要货品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainProduct {

    /**
     * 货物编码
     */
    @Id
    @Column(length = 20)
    private String code;
    @Column(length = 40)
    private String name;
    /**
     * 每台租用费用
     */
    private BigDecimal deposit;
    /**
     * 每台安装费用，没有就0
     */
    private BigDecimal install;
}
