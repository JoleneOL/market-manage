package cn.lmjia.market.core.entity.channel;

import cn.lmjia.market.core.entity.MainGood;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 渠道
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Setter
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Inheritance(strategy = InheritanceType.JOINED)
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    /**
     * 是否为额外；参与了额外渠道的产品在主商城流程中不可用。
     */
    private boolean extra = true;
    /**
     * 设备款折扣率,默认1
     */
    @Column(scale = 9, precision = 10)
    private BigDecimal depositRate = BigDecimal.ONE;
    /**
     * 是否锁定每一个订单允许购买的数量；为null 则不限定
     */
    private Integer lockedAmountPerOrder;

    @OneToMany(mappedBy = "channel")
    private Set<MainGood> mainGoodSet;

    @SuppressWarnings({"JpaDataSourceORMInspection", "SpellCheckingInspection"})
    @Column(name = "DTYPE", insertable = false, updatable = false)
    private String classType;

}
