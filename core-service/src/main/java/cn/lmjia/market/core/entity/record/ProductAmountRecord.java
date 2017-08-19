package cn.lmjia.market.core.entity.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
}
