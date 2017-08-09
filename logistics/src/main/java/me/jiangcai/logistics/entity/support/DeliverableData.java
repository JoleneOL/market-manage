package me.jiangcai.logistics.entity.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author CJ
 */
@EqualsAndHashCode(callSuper = false)
@Embeddable
@Setter
@Getter
public class DeliverableData extends Address {

    @Column(length = 20)
    private String people;
    @Column(length = 20)
    private String mobile;
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

}
