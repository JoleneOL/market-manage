package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.supplier.ManuallySupplier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
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
    private boolean enable = true;
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

    public static Path<String> mobile(From<?, ? extends Depot> depotFrom) {
        return depotFrom.get("chargePeopleMobile");
    }

    public static Path<String> name(From<?, ? extends Depot> depotFrom) {
        return depotFrom.get("chargePeopleName");
    }

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

    /**
     * @return 支持这个仓库的供应商声明类
     */
    public Class<? extends LogisticsSupplier> getSupplierClass() {
        return ManuallySupplier.class;
    }

    /**
     * @return 支持该仓库的物流供应商是否支持安装
     */
    public boolean isInstallationSupport() {
        return false;
    }

}
