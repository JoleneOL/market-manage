package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLimitStockException;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.service.MarketStockService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Product;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class MarketStockServiceImpl implements MarketStockService {

    private static final int defaultOffsetHour = 9;
    private static final Log log = LogFactory.getLog(MarketStockServiceImpl.class);
    //货品的限购数量及清算时间
    private Map<String, OffsetStock> productStockMap = new HashMap<>();
    @Autowired
    private StockService stockService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private Environment environment;

    @Override
    public void cleanProductStock(Product product) {
        if (productStockMap.containsKey(product.getCode())) {
            productStockMap.remove(product.getCode());
        }
    }

    @Override
    public void checkGoodStock(Map<MainGood, Integer> amounts) throws MainGoodLowStockException {
        Map<MainGood, Integer> lowStockProduct = new HashMap<>();
        Map<MainGood, LocalDateTime> relieveTime = new HashMap<>();
        for (MainGood good : amounts.keySet()) {
            int usableStock = usableStock(good.getProduct());
            if (good.getProduct().getPlanSellOutDate() == null && usableStock < amounts.get(good)) {
                lowStockProduct.put(good, usableStock);
            } else if (good.getProduct().getPlanSellOutDate() != null && usableStock < amounts.get(good)) {
                lowStockProduct.put(good, usableStock);
                relieveTime.put(good, getTodayOffsetTime().plusDays(1));
            }
        }
        if (lowStockProduct.size() > 0) {
            throw relieveTime.size() == 0 ? new MainGoodLowStockException(lowStockProduct) : new MainGoodLimitStockException(lowStockProduct, relieveTime);
        }
    }

    @Override
    public int sumProductNum(Product product) {
        return sumProductNum(product, null, null, OrderStatus.forPay, OrderStatus.forDeliver);
    }

    @Override
    public int sumProductNum(Product product, LocalDateTime beginTime, LocalDateTime endTime, OrderStatus... orderStatuses) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MainOrder> root = cq.from(MainOrder.class);
        //今日核算时间之前的订单
        MapJoin<MainOrder, MainGood, Integer> amountsRoot = root.join(MainOrder_.amounts);
        List<Predicate> list = new ArrayList<>();
        list.add(cb.notEqual(root.get(MainOrder_.orderStatus), OrderStatus.close));
        list.add(cb.equal(amountsRoot.key().get(MainGood_.product), product));
        if (orderStatuses != null) {
            list.add(root.get(MainOrder_.orderStatus)
                    .in(orderStatuses));
        }
        if (beginTime != null) {
            list.add(cb.greaterThanOrEqualTo(root.get(MainOrder_.orderTime), beginTime));
        }
        if (endTime != null) {
            list.add(cb.lessThanOrEqualTo(root.get(MainOrder_.orderTime), endTime));
        }
        Predicate[] p = new Predicate[list.size()];
        cq.where(cb.and(list.toArray(p)));
        cq.select(cb.sum(amountsRoot.value()));
        Object result = entityManager.createQuery(cq).getSingleResult();
        return result != null ? (int) result : 0;
    }

    private int limitStock(Product product) {
        //如果已经计算过了，就直接从 map 中获取
        LocalDateTime now = LocalDateTime.now();
        if (productStockMap.containsKey(product.getCode())) {
            OffsetStock offsetStock = productStockMap.get(product.getCode());
            if (offsetStock.getOffsetDate().equals(getOffsetDate())) {
                return offsetStock.getStock();
            }
        }
        long limitDay;
        //如果未设置限购时间，或者限购时间已经超过了，那么货品就不限购
        if (product instanceof MainProduct) {
            LocalDate planSellOutDate = ((MainProduct) product).getPlanSellOutDate();
            if (planSellOutDate == null || planSellOutDate.isBefore(now.toLocalDate())) {
                limitDay = 1L;
            } else {
                limitDay = ChronoUnit.DAYS.between(now.minusHours(getOffsetHour()).toLocalDate(), planSellOutDate) + 1;
            }
        } else {
            limitDay = 1L;

        }
        int totalUsableStock = stockService.usableStockTotal(product);
        //锁定库存包括 代付款，待发货
        int lockedStock = sumProductNum(product);
        int todayStock = sumProductNum(product, getTodayOffsetTime(), null, null);
        //限购数量 = 当前库存总数 - 当前冻结总数 + 今日下单数
        int totalProductStock = totalUsableStock - lockedStock + todayStock;
        int productStock = totalProductStock <= 0 ? 0 : (int) (totalProductStock / limitDay);
        log.debug("Product:" + product.getCode()
                + ";TotalUsableStock:" + totalUsableStock
                + ";lockStock:" + lockedStock
                + ";todayStock:" + todayStock
                + ";limitDay:" + limitDay);
        productStockMap.put(product.getCode(), new OffsetStock(getOffsetDate(), productStock));
        return productStock;
    }

    private int usableStock(Product product) {
        int limitStock = limitStock(product);
        LocalDateTime orderBeginTime = getTodayOffsetTime();
        //计算今日所有未关闭订单的货品数量
        int todayStock = sumProductNum(product, orderBeginTime, null, null);
        log.debug("Product:" + product.getCode() + ";limitStock:" + limitStock + ";lockedStock:" + todayStock);
        return todayStock > limitStock ? 0 : limitStock - todayStock;
    }

    @Override
    public LocalDateTime getTodayOffsetTime() {
        return getOffsetDate().atStartOfDay().plusHours(getOffsetHour());
    }

    /**
     * 获取当前时间相对于清算时间的日期
     * 比如清算时间是9点，现在是8点，那么当前时间相对于清算时间要减1天
     *
     * @return
     */
    private LocalDate getOffsetDate() {
        return LocalDateTime.now().minusHours(getOffsetHour()).toLocalDate();
    }

    @Override
    public void calculateGoodStock(Collection<MainGood> mainGoodSet) {
        mainGoodSet.forEach(mainGood -> mainGood.getProduct().setStock(usableStock(mainGood.getProduct())));
    }

    /**
     * 获取清算时间配置
     *
     * @return
     */
    private int getOffsetHour() {
        int offsetHour = systemStringService.getCustomSystemString("market.core.service.product.offsetHour", null, true, Integer.class, defaultOffsetHour);
        if (!environment.acceptsProfiles(CoreConfig.ProfileUnitTest) && (offsetHour > 23 || offsetHour < 0)) {
            offsetHour = defaultOffsetHour;
        }
        return offsetHour;
    }


    @Getter
    @AllArgsConstructor
    private class OffsetStock {
        //清算日期
        private LocalDate offsetDate;
        private Integer stock;
    }
}
