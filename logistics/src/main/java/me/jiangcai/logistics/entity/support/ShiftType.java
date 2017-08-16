package me.jiangcai.logistics.entity.support;

/**
 * 库存变化类型
 *
 * @author CJ
 */
public enum ShiftType {
    /**
     * 系统增减
     */
    root,
    /**
     * 核准 增减
     */
    audit,
    /**
     * 损耗
     */
    wastage,
    /**
     * 非良品导致的
     */
    badness,
    /**
     * 物流变化导致的
     */
    logistics
}
