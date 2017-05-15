package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * 产品信息
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 货物编码
     */
    @Column(length = 40, nullable = false, unique = true)
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
