package cn.lmjia.market.core.entity.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@Data
@Embeddable
public class ProductAmountRecord {
    @Column(length = 40)
    private String productName;
    @Column(length = 20)
    private String productType;
    private int amount;
    /**
     * 购买时价格
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal price;
    /**
     * 享受佣金 的价格
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal commissioningPrice;
}
