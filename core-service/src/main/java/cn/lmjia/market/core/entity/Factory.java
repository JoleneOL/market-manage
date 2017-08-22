package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 工厂
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Factory implements LogisticsDestination, LogisticsSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上架状态
     */
    private boolean enable = true;

    @Column(columnDefinition = "timestamp", updatable = false)
    private LocalDateTime createTime;

    @Column(length = 100)
    private String name;

    private Address address;
    /**
     * 负责人姓名
     */
    @Column(length = 20)
    private String chargePeopleName;
    /**
     * 负责人电话
     */
    @Column(length = 20)
    private String chargePeopleMobile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Factory)) return false;
        Factory depot = (Factory) o;
        return Objects.equals(id, depot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getProvince() {
        return address.getProvince();
    }

    @Override
    public String getCity() {
        return address.getPrefecture();
    }

    @Override
    public String getCountry() {
        return address.getCounty();
    }

    @Override
    public String getDetailAddress() {
        return address.getOtherAddress();
    }

    @Override
    public String getConsigneeName() {
        return chargePeopleName;
    }

    @Override
    public String getConsigneeMobile() {
        return chargePeopleMobile;
    }
}
