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
 * 包括已结算的库存可用状态
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Table(name = "UsageStock")
@Entity
@Getter
@Setter
public class UsageStock {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "PRODUCT_CODE")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "DEPOT_ID")
    private Depot depot;
    @Column(name = "AMOUNT")
    private int amount;
}
