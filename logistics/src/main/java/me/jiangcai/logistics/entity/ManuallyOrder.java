package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ManuallyOrder extends StockShiftUnit {
    /**
     * 快递订单号
     */
    @Column(length = 40)
    private String orderNumber;
}
