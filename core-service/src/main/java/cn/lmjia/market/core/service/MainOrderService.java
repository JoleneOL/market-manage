package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.exception.UnnecessaryShipException;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.OrderInstalledEvent;
import me.jiangcai.logistics.exception.StockOverrideException;
import me.jiangcai.wx.model.Gender;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * @param amounts            不可以包含数量0的商品！
     * @param mortgageIdentifier 可选的按揭识别码
     * @return 新创建的订单
     */
    @Transactional
    MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress
            , Map<MainGood, Integer> amounts, String mortgageIdentifier);

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
     * @param order 订单
     * @return 享受该订单受益者
     */
    @Transactional(readOnly = true)
    Login getEnjoyability(MainOrder order);

    /**
     * @param orderBy 下单人
     * @return 如果该人下单则何人获得收益
     */
    @Transactional(readOnly = true)
    Login getEnjoyability(Login orderBy);

    /**
     * @param orderId   可选订单号
     * @param mobile    可选购买者手机号码
     * @param goodId    可选商品
     * @param orderDate 可选下单日期
     * @param beginDate 可选的下单起始日期；若orderDate已提供则该值无效
     * @param endDate   可选的下单结束日期；若orderDate已提供则该值无效
     * @param status    可选状态；如果为{@link OrderStatus#EMPTY}表示所有  @return 获取数据规格
     */
    Specification<MainOrder> search(String orderId, String mobile, Long goodId, LocalDate orderDate, LocalDate beginDate, LocalDate endDate, OrderStatus status);

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

    /**
     * @param orderId 订单号
     * @return 这个订单需要的库存信息
     */
    @Transactional(readOnly = true)
    List<Depot> depotsForOrder(long orderId);

    /**
     * 一次性物流开动，并且不进行安装
     *
     * @param supplierType 物流类型
     * @param orderId      订单号
     * @param depotId      仓库号
     * @return 相关信息
     */
    @Transactional
    @Deprecated
    StockShiftUnit makeLogistics(Class<? extends LogisticsSupplier> supplierType, long orderId, long depotId);

    /**
     * 物流开动
     *
     * @param supplierType 物流类型
     * @param orderId      订单号
     * @param depotId      仓库号
     * @param amounts      物流货品；默认为全部货品
     * @param installation 是否进行安装
     * @return 相关信息
     * @throws StockOverrideException   库存不足
     * @throws UnnecessaryShipException 没必要的物流
     */
    @Transactional
    StockShiftUnit makeLogistics(Class<? extends LogisticsSupplier> supplierType, long orderId, long depotId
            , Map<MainProduct, Integer> amounts, boolean installation) throws StockOverrideException
            , UnnecessaryShipException;

    @EventListener(OrderInstalledEvent.class)
    @Transactional
    MainOrderFinishEvent forOrderInstalledEvent(OrderInstalledEvent event);
}
