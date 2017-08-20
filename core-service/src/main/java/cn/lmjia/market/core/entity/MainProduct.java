package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.define.Money;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 主要货品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainProduct extends Product {
    /**
     * 设备款
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal deposit;
    /**
     * 每日服务费
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal serviceCharge;
    /**
     * 服务费
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal install;

    /**
     * 计划售罄日期
     */
    @Column(columnDefinition = "timestamp")
    private LocalDate planSellOutDate;

    public Money getDepositMoney() {
        return new Money(deposit);
    }

    public Money getInstallMoney() {
        return new Money(install);
    }
}
