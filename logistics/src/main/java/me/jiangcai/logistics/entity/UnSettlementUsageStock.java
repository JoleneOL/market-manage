package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 未结算的库存使用状态
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Table(name = "UnSettlementUsageStock")
@Entity
@Getter
@Setter
public class UnSettlementUsageStock {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "PRODUCT_CODE")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "DESTINATION_ID")
    private Depot destination;
    @ManyToOne
    @JoinColumn(name = "ORIGIN_ID")
    private Depot origin;
    @Column(name = "AMOUNT")
    private int amount;

}
