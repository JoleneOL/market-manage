package cn.lmjia.market.core.entity.record;

import cn.lmjia.market.core.entity.MainGood;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 完整的下单记录
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainOrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Id
//    private Long id;
    @Column(length = 50)
    private String name;
    private int age;
    private Gender gender;
    private Address installAddress;
    @Column(length = 20)
    private String mobile;
    @Deprecated
    @Column(length = 40)
    private String productName;
    @Deprecated
    @Column(length = 20)
    private String productType;
    @Deprecated
    private int amount;
    /**
     * @since {@link cn.lmjia.market.core.Version#muPartOrder}
     */
    @ElementCollection
    private List<ProductAmountRecord> amountRecord;

    /**
     * 按揭识别码
     */
    @Column(length = 32)
    private String mortgageIdentifier;
    @Column(length = 20)
    private String recommendByMobile;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime orderTime;

    public void updateAmounts(Map<MainGood, Integer> amounts) {
        amountRecord = amounts.entrySet().stream()
                .map(entry -> {
                    ProductAmountRecord record = new ProductAmountRecord();
                    record.setProductName(entry.getKey().getProduct().getName());
                    record.setProductType(entry.getKey().getProduct().getCode());
                    record.setAmount(entry.getValue());
                    record.setPrice(entry.getKey().getTotalPrice());
                    record.setCommissioningPrice(entry.getKey().getProduct().getDeposit());
                    return record;
                })
                .collect(Collectors.toList());
    }
}
