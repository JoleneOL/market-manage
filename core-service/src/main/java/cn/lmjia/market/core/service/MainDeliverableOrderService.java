package cn.lmjia.market.core.service;

import cn.lmjia.market.core.aop.MultiBusinessSafe;
import cn.lmjia.market.core.aop.MultipleBusinessLocker;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import lombok.Data;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.LogisticsHostService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.wx.model.Gender;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
public interface MainDeliverableOrderService<T extends MainDeliverableOrder> extends LogisticsHostService {

    /**
     * @return 是否支持字符串的orderId
     */
    default boolean supportOrderId() {
        return false;
    }

    default Predicate orderIdPredicate(String orderId, Root<T> root, CriteriaBuilder cb) {
        return cb.conjunction();
    }

    /**
     * @return 不言而喻
     */
    Class<T> getOrderClass();

    /**
     * @param id 订单主键
     * @return 订单, never null
     */
    @Transactional(readOnly = true)
    T getOrder(long id);

    /**
     * @param orderId 订单号
     * @return 这个订单需要的库存信息
     */
    @Transactional(readOnly = true)
    List<Depot> depotsForOrder(long orderId);

    /**
     * 新创建订单
     *
     * @param who                创建者，也将是支付者
     * @param recommendBy        推荐人，必有的
     * @param name               客户
     * @param mobile             客户手机
     * @param age                年龄
     * @param gender             性别
     * @param installAddress     安装地址
     * @param amounts            不可以包含数量0的商品！
     * @param mortgageIdentifier 可选的按揭识别码
     * @return 新创建的订单
     */
    @Transactional
    T newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress
            , Map<MainGood, Integer> amounts, String mortgageIdentifier) throws MainGoodLowStockException;

    @MultiBusinessSafe
    T newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress
            , MainOrderService.Amounts amounts, String mortgageIdentifier) throws MainGoodLowStockException;


    /**
     * @param orderId   可选订单号
     * @param mobile    可选购买者手机号码
     * @param goodId    可选商品
     * @param orderDate 可选下单日期
     * @param beginDate 可选的下单起始日期；若orderDate已提供则该值无效
     * @param endDate   可选的下单结束日期；若orderDate已提供则该值无效
     * @param status    可选状态；如果为{@link OrderStatus#EMPTY}表示所有  @return 获取数据规格
     */
    Specification<T> search(String orderId, String mobile, Long goodId, LocalDate orderDate
            , LocalDate beginDate, LocalDate endDate, OrderStatus status);

    /**
     * 全文搜索
     *
     * @param search 可选的搜索字段
     * @param status 可选状态；如果为{@link OrderStatus#EMPTY}表示所有
     * @return 获取数据规格
     */
    Specification<T> search(String search, OrderStatus status);

    // 内部API
    @Data
    class Amounts implements MultipleBusinessLocker {
        private final Map<MainGood, Integer> amounts;

        @Override
        public Object[] toLock() {
            return amounts.keySet().stream()
                    .map(mainGood -> ("MainGoodStockLock-" + mainGood.getProduct().getCode()).intern())
                    .toArray(Object[]::new);
        }
    }
}
