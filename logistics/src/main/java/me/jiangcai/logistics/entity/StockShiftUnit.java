package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.PersistingReadable;
import me.jiangcai.logistics.entity.support.DeliverableData;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.OrderBy;
import javax.persistence.Transient;
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
@SuppressWarnings("JpaDataSourceORMInspection")
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
     * 该物流是否需要安装，总是应用于出库订单
     */
    private boolean installation;
    /**
     * 可选的来源仓库；
     */
    @ManyToOne
    private Depot origin;
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "people", column = @Column(name = "ORIGIN_PEOPLE", length = 20))
                    , @AttributeOverride(name = "mobile", column = @Column(name = "ORIGIN_MOBILE", length = 20))
                    , @AttributeOverride(name = "province", column = @Column(name = "ORIGIN_PROVINCE", length = 20))
                    , @AttributeOverride(name = "prefecture", column = @Column(name = "ORIGIN_PREFECTURE", length = 20))
                    , @AttributeOverride(name = "county", column = @Column(name = "ORIGIN_COUNTRY", length = 20))
                    , @AttributeOverride(name = "otherAddress", column = @Column(name = "ORIGIN_OTHER_ADDRESS", length = 100))
            }
    )
    private DeliverableData originData;
    /**
     * 可选的目的仓库；
     */
    @ManyToOne
    private Depot destination;
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "people", column = @Column(name = "DESTINATION_PEOPLE", length = 20))
                    , @AttributeOverride(name = "mobile", column = @Column(name = "DESTINATION_MOBILE", length = 20))
                    , @AttributeOverride(name = "province", column = @Column(name = "DESTINATION_PROVINCE", length = 20))
                    , @AttributeOverride(name = "prefecture", column = @Column(name = "DESTINATION_PREFECTURE", length = 20))
                    , @AttributeOverride(name = "county", column = @Column(name = "DESTINATION_COUNTRY", length = 20))
                    , @AttributeOverride(name = "otherAddress", column = @Column(name = "DESTINATION_OTHER_ADDRESS", length = 100))
            }
    )
    private DeliverableData destinationData;
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
    @OrderBy("time asc")
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

    public void addStatus(LocalDateTime time, String message, ShiftStatus status, PersistingReadable source) {
        setCurrentStatus(status);
        setLastStatusTime(time);
        if (events == null)
            events = new HashMap<>();
        StockShiftUnitEvent event = new StockShiftUnitEvent();
        event.setMessage(message);
        event.setTime(time);
        event.setToStatus(status);
        event.setSource(source);
        events.put(time, event);
    }

    /**
     * @return 负责提供物流服务的公司
     */
    @Transient
    public String getSupplierOrganizationName() {
        return null;
    }

    /**
     * @return 检查下 是否需要安装
     */
    public boolean checkInstallation() {
        return amounts.entrySet().stream()
                .filter(entry -> entry.getValue().getAmount() > 0)
                .filter(entry -> entry.getKey().isInstallation())
                .count() > 0;
    }
    /**
     * @return 是否仅仅为入库单
     */
    public boolean isJustWarehousing() {
        return origin == null && destination != null;
    }
}
