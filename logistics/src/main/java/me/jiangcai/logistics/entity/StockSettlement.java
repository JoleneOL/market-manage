package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 库存结算状态
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
    @ManyToOne
    private Depot depot;
    @ManyToOne
    private Product product;
    /**
     * 结算库存量
     */
    private int stock;
}
