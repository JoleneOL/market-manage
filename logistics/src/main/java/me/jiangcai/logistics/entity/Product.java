package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import java.math.BigDecimal;

/**
 * 货品
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    /**
     * 货物编码|产品编码
     */
    @Id
    @Column(length = 20)
    private String code;
    /**
     * 产品名称
     */
    @Column(length = 40)
    private String name;
    /**
     * 品牌
     */
    @Column(length = 100)
    private String brand;

    /**
     * 主类目
     */
    @Column(length = 100)
    private String mainCategory;

    /**
     * 简易描述
     */
    @Lob
    private String description;
    /**
     * 富文本描述
     */
    @Lob
    private String richDescription;

    @Column(length = 80)
    private String SKU;
    @Column(length = 10)
    private String unit;

    /**
     * 长度，单位mm
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal volumeLength;
    /**
     * 宽度，单位mm
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal volumeWidth;
    /**
     * 高度，单位mm
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal volumeHeight;
    /**
     * 重量，单位g
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal weight;

    /**
     * @return 体积，单位mm'3
     */
    public BigDecimal getVolume() {
        return getVolumeLength().multiply(getVolumeWidth()).multiply(getVolumeHeight());
    }
}
