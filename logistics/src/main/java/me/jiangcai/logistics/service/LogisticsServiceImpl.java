package me.jiangcai.logistics.service;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockSettlement;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logistics.repository.StockSettlementRepository;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private StockSettlementRepository stockSettlementRepository;

    @Override
    public StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination) {
        return makeShift(supplier, things, source, destination, false);
    }

    private StockShiftUnit makeShift(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source
            , LogisticsDestination destination, boolean installation) {
        // 不同的供应商可能对于地址有不同的要求
        if (supplier == null) {
            supplier = applicationContext.getBean(LogisticsSupplier.class);
        }
        // 如果Source是个仓库 则表示出库
        int options = (source instanceof Depot) ? LogisticsOptions.CargoFromStorage : 0;
        if (installation)
            options = options | LogisticsOptions.Installation;
        Consumer<StockShiftUnit> consumer = stockShiftUnit -> {
            stockShiftUnit.setCreateTime(LocalDateTime.now());
            stockShiftUnit.setCurrentStatus(ShiftStatus.init);
            stockShiftUnit.setLastStatusTime(stockShiftUnit.getCreateTime());
            if (source instanceof Depot)
                stockShiftUnit.setOrigin((Depot) source);
            if (destination instanceof Depot) {
                stockShiftUnit.setDestination((Depot) destination);
            }

            stockShiftUnit.setAmounts(things.stream()
                    .collect(Collectors.toMap(Thing::getProduct, Thing::getAmount)));
        };
        return stockShiftUnitRepository.save(supplier.makeDistributionOrder(source, things, destination, options, consumer));
    }

    @Override
    public StockShiftUnit makeShiftWithInstallation(LogisticsSupplier supplier, Collection<Thing> things, LogisticsSource source, LogisticsDestination destination) {
        return makeShift(supplier, things, source, destination, true);
    }

    @Override
    public int usableStock(Depot depot, Product product) {
        // 获取 该库存的最新结算量
        StockSettlement settlement = lastStockSettlement(depot, product);
        // 获取 其后的和未结算的进出库订单合计
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<StockShiftUnit> root = cq.from(StockShiftUnit.class);
        MapJoin<StockShiftUnit, Product, Integer> amountJoin = root.joinMap("amounts");
        amountJoin = amountJoin.on(cb.equal(amountJoin.key(), product));

        Join<StockShiftUnit, Depot> inDepot = root.join("destination", JoinType.LEFT);
        Join<StockShiftUnit, Depot> outDepot = root.join("origin", JoinType.LEFT);
        Expression<LocalDateTime> time = root.get("lockedTime");
        Expression<ShiftStatus> status = root.get("currentStatus");
        //进入-出去
        //
        Expression<Number> flag = cb.<Boolean, Number>selectCase(cb.equal(inDepot, depot))
                .when(true, 1)
                .otherwise(-1);

        Expression<Number> incoming = cb.sum(cb.prod(flag, amountJoin.value()));
        return
                settlement.getStock() +
                        entityManager.createQuery(
                                cq.select(incoming)
                                        .where(
                                                cb.and(
                                                        // 库存相关的
                                                        cb.or(
                                                                // 入库则是只有成功入了才算入！
                                                                cb.and(
                                                                        cb.equal(inDepot, depot)
                                                                        , cb.equal(status, ShiftStatus.success)
                                                                )
                                                                // 出库是只要出了 就算出
                                                                , cb.and(
                                                                        cb.equal(outDepot, depot)
                                                                        , cb.notEqual(status, ShiftStatus.reject)
                                                                )
                                                        )
                                                        // 结算时间合适的
                                                        , cb.or(
                                                                time.isNull()
                                                                , cb.greaterThan(time, settlement.getTime())
                                                        )
                                                )
                                        )
                        )
                                .getResultList().stream()
                                .filter(Objects::nonNull)
                                .mapToInt(Number::intValue).sum();
    }

    @Override
    public void addStock(Depot depot, Product product, int amount, String message) {
        StockShiftUnit unit = new StockShiftUnit();
        unit.setDestination(depot);
        unit.setCreateTime(LocalDateTime.now());
        unit.setLastStatusTime(unit.getCreateTime());
        unit.setCurrentStatus(ShiftStatus.success);
        unit.setShiftType(ShiftType.root);
        unit.setMessage(message);
        unit.addAmount(product, amount);
        stockShiftUnitRepository.save(unit);
    }

    /**
     * @param depot   库存
     * @param product 货品
     * @return 最新的结算信息;如果不存在会返回一个世纪前的一个结算点，结算量是0
     */
    private StockSettlement lastStockSettlement(Depot depot, Product product) {
        StockSettlement settlement = stockSettlementRepository.findTop1ByDepotAndProductOrderByTimeDesc(depot, product);
        if (settlement == null) {
            settlement = new StockSettlement();
            settlement.setDepot(depot);
            settlement.setProduct(product);
            settlement.setStock(0);
            settlement.setTime(LocalDateTime.now().minus(1, ChronoUnit.MILLENNIA));
        }
        return settlement;
    }
}
