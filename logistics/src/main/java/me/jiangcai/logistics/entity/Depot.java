package me.jiangcai.logistics.entity;

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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class Depot implements LogisticsDestination, LogisticsSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上架状态
     */
    private boolean enable;

    @Column(columnDefinition = "timestamp")
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

    @SuppressWarnings({"JpaDataSourceORMInspection", "SpellCheckingInspection"})
    @Column(name = "DTYPE", insertable = false, updatable = false)
    private String classType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Depot)) return false;
        Depot depot = (Depot) o;
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
