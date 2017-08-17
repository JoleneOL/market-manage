package cn.lmjia.market.core.entity.order;

import cn.lmjia.market.core.entity.Manager;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 手动物流订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ManualOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    @ManyToOne
    private Manager creator;
    private int amount;
    @Column(scale = 2, precision = 12)
    private BigDecimal price;
    @Column(length = 20)
    private String name;
    @Column(length = 20)
    private String mobile;
    private Address address;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdTime;
    @ManyToOne
    private StockShiftUnit shiftUnit;


}
