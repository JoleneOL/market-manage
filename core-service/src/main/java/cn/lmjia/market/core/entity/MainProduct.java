package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * 主要货品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainProduct extends Product {
    /**
     * 每台保证金
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal deposit;
    /**
     * 每日服务费
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal serviceCharge;
    /**
     * 每台安装费用，没有就0
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal install;
}
