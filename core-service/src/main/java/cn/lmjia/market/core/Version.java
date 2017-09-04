package cn.lmjia.market.core;

/**
 * @author CJ
 */
public enum Version {
    init,
    /**
     * 订单包含多样商品版本
     */
    muPartOrder,
    /**
     * 新的登录体系
     * 在登录关系中不再存在所谓200的客户关系
     */
    newLogin,
    /**
     * 一个订单支持多个物流
     */
    muPartShift
}
