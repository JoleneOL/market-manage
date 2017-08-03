package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Entity
@Setter
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    /**
     * 是否为额外；参与了额外渠道的产品在主商城流程中不可用。
     */
    private boolean extra;
    /**
     * 设备款折扣率
     */
    @Column(scale = 9, precision = 10)
    private BigDecimal depositRate;

    @OneToMany(mappedBy = "channel")
    private Set<MainGood> mainGoodSet;

}
