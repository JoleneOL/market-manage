package cn.lmjia.market.core.entity.record;

import cn.lmjia.market.core.entity.support.Address;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.Gender;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 完整的下单记录
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class OrderRecord {
    @Id
    private Long id;
    @Column(length = 50)
    private String name;
    private int age;
    private Gender gender;
    private Address installAddress;
    @Column(length = 20)
    private String mobile;
    private String productName;
    private String productType;
    private int amount;
    /**
     * 按揭识别码
     */
    @Column(length = 32)
    private String mortgageIdentifier;
    @Column(length = 20)
    private String recommendByMobile;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime orderTime;
}
