package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 库存移动的最小单位
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class StockShiftUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 转移的货品以及数量
     */
    @ElementCollection
    private Map<Product, ProductBatch> amounts;
    /**
     * 可选的来源仓库；
     */
    @ManyToOne
    private Depot origin;
    /**
     * 可选的目的仓库；
     */
    @ManyToOne
    private Depot destination;
    /**
     * 建立本次改变的时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createTime;
    /**
     * 当前状态
     */
    private ShiftStatus currentStatus;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime lastStatusTime;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @MapKey(name = "time")
    private Map<LocalDateTime, StockShiftUnitEvent> events;
    private ShiftType shiftType;
    /**
     * 额外消息
     */
    @Column(length = 100)
    private String message;
    /**
     * 锁定，即被结算；为null表示未被锁定。
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime lockedTime;
    @SuppressWarnings({"JpaDataSourceORMInspection", "SpellCheckingInspection"})
    @Column(name = "DTYPE", insertable = false, updatable = false)
    private String classType;

    public static <T extends StockShiftUnit> Join<T, Depot> destinationJoin(From<?, T> from) {
        return from.join("destination", JoinType.LEFT);
    }

    public static <T extends StockShiftUnit> Join<T, Depot> originJoin(From<?, T> from) {
        return from.join("origin", JoinType.LEFT);
    }

    public static Path<LocalDateTime> createDate(From<?, ? extends StockShiftUnit> from) {
        return from.get("createTime");
    }

    public void addAmount(Product product, int amount) {
        addAmount(product, ProductStatus.normal, amount);
    }

    @SuppressWarnings("WeakerAccess")
    public void addAmount(Product product, ProductStatus status, int amount) {
        if (amounts == null) {
            amounts = new HashMap<>();
        }
        if (amounts.containsKey(product)) {
            throw new IllegalStateException("一次转移只能处理货品的一种状态");
        } else
            amounts.put(product, new ProductBatch(status, amount));
    }

    public void addStatus(LocalDateTime time, String message, ShiftStatus status) {
        setCurrentStatus(status);
        setLastStatusTime(time);
        if (events == null)
            events = new HashMap<>();
        StockShiftUnitEvent event = new StockShiftUnitEvent();
        event.setMessage(message);
        event.setTime(time);
        event.setToStatus(status);
        events.put(time, event);
    }
}
