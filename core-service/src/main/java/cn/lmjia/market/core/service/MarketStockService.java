package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import me.jiangcai.logistics.entity.Product;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 销售库存服务
 *
 * @author CJ
 */
public interface MarketStockService {

    /**
     * 商品修改计划售罄时间后，需要调用这个方法来实时生效
     *
     * @param product 货品
     */
    void cleanProductStock(Product product);

    void checkGoodStock(Map<MainGood, Integer> amounts) throws MainGoodLowStockException;

    /**
     * 指定货品冻结库存：订单状态为{未支付，未发货}的订单货品数
     *
     * @param product 指定货品
     * @return 货品冻结库存
     */
    @Transactional(readOnly = true)
    int sumProductNum(Product product);

    /**
     * 指定条件的订单货品总数
     *
     * @param product       指定货品
     * @param beginTime     订单区间起始时间，包含
     * @param endTime       订单区间结束时间，包含
     * @param orderStatuses 订单指定状态
     * @return 订单货品总数
     */
    @Transactional(readOnly = true)
    int sumProductNum(Product product, LocalDateTime beginTime, LocalDateTime endTime, OrderStatus... orderStatuses);

//    /**
//     * 计算今日可销售库存 = 今日核算时间开始时的可用库存 / 计划售罄天数 - 今日销售数量
//     *
//     * @param product 主要货品
//     * @return 限购库存
//     */
//    @Transactional(readOnly = true)
//    int limitStock(Product product);
//
//    /**
//     * 计算指定货品的可销售库存 = 今日限购库存 - 今日订单库存
//     *
//     * @param product 指定货品
//     * @return 可用库存
//     */
//    @Transactional(readOnly = true)
//    int usableStock(Product product);

    /**
     * 对货品的库存赋值
     *
     * @param mainGoodSet 商品列表
     */
    @Transactional(readOnly = true)
    void calculateGoodStock(Collection<MainGood> mainGoodSet);

    LocalDateTime getTodayOffsetTime();

}
