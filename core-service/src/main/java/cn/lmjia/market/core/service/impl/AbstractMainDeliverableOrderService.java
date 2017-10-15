package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder_;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainDeliverableOrderService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.MarketStockService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.UsageStock_;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractMainDeliverableOrderService<T extends MainDeliverableOrder>
        implements MainDeliverableOrderService<T> {

    private static final Log log = LogFactory.getLog(AbstractMainDeliverableOrderService.class);
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private MarketStockService marketStockService;
    @Autowired
    private StockService stockService;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Specification<T> search(String orderId, String mobile, Long goodId, LocalDate orderDate, LocalDate beginDate
            , LocalDate endDate, OrderStatus status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (!StringUtils.isEmpty(orderId) && supportOrderId()) {
                log.debug("search order with orderId:" + orderId);
                //前面8位是 时间
                predicate = cb.and(predicate, orderIdPredicate(orderId, root, cb));
            } else if (orderDate != null) {
                log.debug("search order with orderDate:" + orderDate);
                predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get(MainDeliverableOrder_.orderTime), orderDate.toString()));
            } else {
                // 日期过滤
                if (beginDate != null) {
                    predicate = cb.and(predicate, JpaFunctionUtils.ymd(cb, root.get(MainDeliverableOrder_.orderTime), beginDate
                            , CriteriaBuilder::greaterThanOrEqualTo));
                }
                if (endDate != null)
                    predicate = cb.and(predicate, JpaFunctionUtils.ymd(cb, root.get(MainDeliverableOrder_.orderTime), endDate
                            , CriteriaBuilder::lessThanOrEqualTo));
            }
            if (!StringUtils.isEmpty(mobile)) {
                log.debug("search order with mobile:" + mobile);
                // 2个都可以
                predicate = cb.and(predicate, cb.like(Customer.getMobile(root.get(MainDeliverableOrder_.customer)), "%" + mobile + "%"));
            }
            if (goodId != null) {
                root.fetch(MainDeliverableOrder_.amounts);
                predicate = cb.and(predicate, cb.equal(root.join(MainDeliverableOrder_.amounts).key().get(MainGood_.id), goodId));
            }
            if (status != null && status != OrderStatus.EMPTY) {
                if (status == OrderStatus.forDeliver) {
                    predicate = cb.and(predicate, cb.or(
                            cb.equal(root.get(MainDeliverableOrder_.orderStatus), status)
                            , cb.and(
                                    cb.equal(root.get(MainDeliverableOrder_.orderStatus), OrderStatus.forDeliverConfirm)
                                    , cb.equal(root.get(MainDeliverableOrder_.ableShip), true)
                            )
                    ));
                } else
                    predicate = cb.and(predicate, cb.equal(root.get(MainDeliverableOrder_.orderStatus), status));
            }

            return predicate;
        };
    }

    @Override
    public Specification<T> search(String search, OrderStatus status) {
        if (StringUtils.isEmpty(search) && (status == null || status == OrderStatus.EMPTY))
            return null;
        return (root, query, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (!StringUtils.isEmpty(search)) {
                log.debug("search order with mobile:" + search);
                // 2个都可以
                predicate = cb.and(predicate, cb.like(Customer.getMobile(root.get(MainDeliverableOrder_.customer))
                        , "%" + search + "%"));
            }

            if (status != null && status != OrderStatus.EMPTY) {
                predicate = cb.and(predicate, cb.equal(root.get("orderStatus"), status));
            }

            return predicate;
        };
    }

    @Override
    public DeliverableOrder orderFor(StockShiftUnit unit) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getOrderClass());
        Root<T> root = cq.from(getOrderClass());
        try {
            return entityManager.createQuery(cq
                    .where(cb.isMember(unit, root.get("logisticsSet")))
            )
                    .getSingleResult();
        } catch (NoResultException ignored) {
//            log.error("居然没有这个订单！我们还做别的生意么?" + unit.getId(), ignored);
            return null;
        }
    }

    @Override
    public List<Depot> depotsForOrder(long orderId) {
        T order = getOrder(orderId);
        // 库存多的优先
        return stockService.usableDepotFor((cb, root)
                -> cb.and(
                order.getAmounts().entrySet().stream()
                        .map(entry -> cb.and(
                                cb.equal(root.get(UsageStock_.product), entry.getKey().getProduct())
                                , cb.greaterThanOrEqualTo(root.get(UsageStock_.amount), entry.getValue())
                        ))
                        .toArray(Predicate[]::new)
        ));
//        final MainProduct product = order.getGood().getProduct();
//        return stockService.enabledUsableStockInfo(((productPath, criteriaBuilder)
//                -> criteriaBuilder.equal(productPath, product)), null)
//                .forProduct(product);
    }

    @Override
    public T newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, MainOrderService.Amounts amounts, String mortgageIdentifier)
            throws MainGoodLowStockException {
        // 客户处理
        Customer customer = customerService.getNoNullCustomer(name, mobile, loginService.lowestAgentLevel(who)
                , recommendBy);
        //检查货品库存数量
        marketStockService.checkGoodStock(amounts.getAmounts());

        customer.setInstallAddress(installAddress);
        customer.setGender(gender);
        final LocalDate now = LocalDate.now();
        customer.setBirthYear(now.getYear() - age);

        T order = newOrder(who, recommendBy);

        order.setAmounts(amounts.getAmounts());
        order.setCustomer(customer);
        order.setInstallAddress(installAddress);
//        order.setMortgageIdentifier(mortgageIdentifier);
        order.setOrderTime(LocalDateTime.now());
//        order.setGood(good);
        order.makeRecord();

        return persistOrder(order, mortgageIdentifier);
    }

    /**
     * @param order              已完成初始化的订单
     * @param mortgageIdentifier 可选的按揭码
     * @return 返回完成持久化的订单
     */
    protected abstract T persistOrder(T order, String mortgageIdentifier);

    /**
     * @param who         下单者
     * @param recommendBy 已经没用的推荐者
     * @return 初始化一个order
     */
    protected abstract T newOrder(Login who, Login recommendBy);


}
