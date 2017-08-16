package me.jiangcai.logistics.entity.support;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * @author CJ
 */
@Data
@Embeddable
@NoArgsConstructor
public class StockInfo {
    @ManyToOne
    private Depot depot;
    @ManyToOne
    private Product product;
    private int amount;

    @SuppressWarnings("unused")
    public StockInfo(Depot depot, Product product, Number amount) {
        this.depot = depot;
        this.product = product;
        if (amount != null)
            this.amount = amount.intValue();
        else
            this.amount = 0;
    }
}
