package cn.lmjia.market.core.entity.support;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * @author CJ
 */
@Data
@Embeddable
public class Address {
    /**
     * 省(province)/直辖市(municipality)/自治区(autonomous region)/特别行政区(special administrative region/SAR)
     */
    @Column(length = 20)
    private String province;
    /**
     * 地级市(prefecture-level city)/地区(prefecture)/自治州(autonomous prefecture)/盟(league)
     */
    @Column(length = 20)
    private String prefecture;
    /**
     * 县(county)/自治县(autonomous county)/县级市(county-level  city)/市辖区(district)/旗(banner)
     * /自治旗(autonomous banner)/林区(forestry area)/特区(special district)
     */
    @Column(length = 20)
    private String county;
    @Column(length = 100)
    private String otherAddress;

    /**
     * @param addressPath     地址的path
     * @param address         地址信息
     * @param criteriaBuilder cb
     * @return 是否地址大致一致
     */
    public static Predicate AlmostMatch(Path<Address> addressPath, Address address, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(addressPath.get("province"), address.getProvince())
                , criteriaBuilder.equal(addressPath.get("prefecture"), address.getPrefecture())
                , criteriaBuilder.equal(addressPath.get("county"), address.getCounty())
        );
    }

    @Override
    public String toString() {
        return province + "-" + prefecture + "-" + county + otherAddress;
    }

    /**
     * @return 把除了otherAddress之外的地址组织成一个标准格式
     */
    public String getStandardWithoutOther() {
        return province + "/" + prefecture + "/" + county;
    }

    public String toTRJString() {
        return province + "-" + prefecture + "-" + county + "-" + otherAddress;
    }
}
