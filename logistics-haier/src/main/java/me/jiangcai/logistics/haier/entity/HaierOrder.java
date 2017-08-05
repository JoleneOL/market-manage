package me.jiangcai.logistics.haier.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.StockShiftUnit;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 出库 delivery order
 * 入库 stock debit notes
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class HaierOrder extends StockShiftUnit {
    @Column(length = 32)
    private String orderNumber;
}
