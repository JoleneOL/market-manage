package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.support.StockInfo;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 库存结算状态
 * 结算是与仓库和货品无关的
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class StockSettlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 结算时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime time;
    @Column(length = 30)
    private String comment;
    @ElementCollection
    private Set<StockInfo> usableStock;

    public int usableStock(Depot depot, Product product) {
        if (usableStock == null)
            return 0;
        return usableStock.stream()
                .filter(stockInfo -> stockInfo.getDepot().equals(depot) && stockInfo.getProduct().equals(product))
                .findFirst().orElse(new StockInfo()).getAmount();
    }
}
