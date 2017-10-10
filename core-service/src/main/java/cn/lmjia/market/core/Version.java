package cn.lmjia.market.core;

/**
 * 合并该文件时，请必须注意新增的（未发布的）必须在已发布的后面！！
 *
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
    muPartShift,
    /**
     * 促销人员体系
     */
    salesman,
    /**
     * 商城
     */
    mall,
    deleteProduct,
}
