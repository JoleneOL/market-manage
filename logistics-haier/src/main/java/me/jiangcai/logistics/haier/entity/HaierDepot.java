package me.jiangcai.logistics.haier.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.haier.HaierSupplier;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class HaierDepot extends Depot {
    /**
     * 海尔（日日顺）仓库编码：按日日顺C码
     */
    @Column(length = 32)
    private String haierCode;

    @Override
    public Class<? extends LogisticsSupplier> getSupplierClass() {
        return HaierSupplier.class;
    }

    @Override
    public boolean isInstallationSupport() {
        return true;
    }
}
