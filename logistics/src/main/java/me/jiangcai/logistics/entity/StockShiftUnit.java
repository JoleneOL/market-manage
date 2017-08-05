package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
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
    private Map<Product, Integer> amounts;

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

    public void addAmount(Product product, int amount) {
        if (amounts == null) {
            amounts = new HashMap<>();
        }
        if (amounts.containsKey(product)) {
            amounts.put(product, amounts.get(product) + amount);
        } else
            amounts.put(product, amount);
    }
}
