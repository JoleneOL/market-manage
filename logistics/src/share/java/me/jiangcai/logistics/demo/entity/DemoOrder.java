package me.jiangcai.logistics.demo.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author CJ
 */
@Setter
@Getter
@Entity
public class DemoOrder implements DeliverableOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ElementCollection
    private Map<Product, Integer> amounts;
    @OneToMany
    private List<StockShiftUnit> stockShiftUnits;
    @OneToMany
    @JoinTable(name = "MAINORDER_INSTALLED_STOCKSHIFTUNIT")
    private List<StockShiftUnit> installedStockShiftUnits;
    private String lastStatus;
    private boolean ableShip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemoOrder)) return false;
        DemoOrder order = (DemoOrder) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Map<? extends Product, Integer> getTotalShipProduct() {
        return amounts;
    }

    @Override
    public List<StockShiftUnit> getShipStockShiftUnit() {
        return stockShiftUnits;
    }

    @Override
    public void switchToForInstallStatus() {
        lastStatus = "switchToForInstallStatus";
    }

    @Override
    public void switchToForDeliverStatus() {
        lastStatus = "switchToForDeliverStatus";
    }

    @Override
    public void setAbleShip(boolean b) {
        ableShip = b;
    }

    @Override
    public void switchToLogisticsFinishStatus() {
        lastStatus = "switchToLogisticsFinishStatus";
    }

    @Override
    public void addInstallStockShiftUnit(StockShiftUnit unit) {
        if (installedStockShiftUnits == null)
            installedStockShiftUnits = new ArrayList<>();
        installedStockShiftUnits.add(unit);
    }

    @Override
    public List<? extends StockShiftUnit> getInstallStockShiftUnit() {
        return installedStockShiftUnits;
    }
}
