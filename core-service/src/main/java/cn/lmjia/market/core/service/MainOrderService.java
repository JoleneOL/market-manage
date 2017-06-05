package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.OrderStatus;
import me.jiangcai.wx.model.Gender;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
public interface MainOrderService {

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
     * @param good               商品
     * @param amount             数量
     * @param mortgageIdentifier 可选的按揭识别码
     * @return 新创建的订单
     */
    @Transactional
    MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress
            , MainGood good
            , int amount, String mortgageIdentifier);

    /**
     * @return 所有订单
     */
    @Transactional(readOnly = true)
    List<MainOrder> allOrders();

    /**
     * @param id 订单id
     * @return 获取订单，never null
     */
    @Transactional(readOnly = true)
    MainOrder getOrder(long id);

    /**
     * @param orderId {@link MainOrder#getSerialId(Path, CriteriaBuilder)}
     * @return 获取订单，never null
     */
    @Transactional(readOnly = true)
    MainOrder getOrder(String orderId);

    /**
     * @param id 订单id
     * @return 订单是否已支付
     */
    @Transactional(readOnly = true)
    boolean isPaySuccess(long id);

    /**
     * @param orderId   可选订单号
     * @param mobile    可选购买者手机号码
     * @param goodId    可选商品
     * @param orderDate 可选下单日期
     * @param status    可选状态；如果为{@link OrderStatus#EMPTY}表示所有
     * @return 获取数据规格
     */
    Specification<MainOrder> search(String orderId, String mobile, Long goodId, LocalDate orderDate, OrderStatus status);

    /**
     * 全文搜索
     *
     * @param search 可选的搜索字段
     * @param status 可选状态；如果为{@link OrderStatus#EMPTY}表示所有
     * @return 获取数据规格
     */
    Specification<MainOrder> search(String search, OrderStatus status);

    @Transactional
    void updateOrderTime(LocalDateTime time);


}
