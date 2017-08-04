package me.jiangcai.logistics;

import java.math.BigDecimal;

/**
 * @author CJ
 */
public interface Product {
    /**
     * @return 产品编码
     */
    String getCode();

    /**
     * @return 品牌
     */
    String getBrand();

    /**
     * @return 类目
     */
    String getCategory();

    /**
     * @return 描述
     */
    String getDescription();

    /**
     * @return SKU
     */
    String getSKU();

    /**
     * @return 单位
     */
    String getUnit();

    /**
     * @return 长度，单位mm
     */
    BigDecimal getVolumeLength();

    /**
     * @return 宽度，单位mm
     */
    BigDecimal getVolumeWidth();

    /**
     * @return 高度，单位mm
     */
    BigDecimal getVolumeHeight();

    /**
     * @return 体积，单位mm'3
     */
    default BigDecimal getVolume() {
        return getVolumeLength().multiply(getVolumeWidth()).multiply(getVolumeHeight());
    }

    /**
     * @return 重量，单位g
     */
    BigDecimal getWeight();
}
