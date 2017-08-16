package me.jiangcai.logistics.haier.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.StockShiftUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 出库 delivery order
 * 入库 stock debit notes
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Setter
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "orderNumber")})
public class HaierOrder extends StockShiftUnit {
    @Column(length = 32)
    private String orderNumber;
    /**
     * 快递单号
     * 运单号：自动分配的快递单号或客户生成的快递单号
     */
    @Column(length = 32)
    private String expressId;

    @Override
    public String getSupplierOrganizationName() {
        return "青岛日日顺物流有限公司";
    }
}
