package me.jiangcai.logistics.service;

import me.jiangcai.lib.jdbc.JdbcService;
import me.jiangcai.logistics.StockInfoSet;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockSettlement;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.UnSettlementUsageStock;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.entity.support.ShiftType;
import me.jiangcai.logistics.entity.support.StockInfo;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.repository.StockSettlementRepository;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author CJ
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Log log = LogFactory.getLog(StockServiceImpl.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private StockSettlementRepository stockSettlementRepository;
    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private JdbcService jdbcService;

    @Override
    public StockInfoSet enabledUsableStock() {
        return enabledUsableStockInfo(null, null);
    }

    @Override
    public StockInfoSet enabledUsableStockInfo(BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpec
            , BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpec) {
        final BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpecFinal;
        if (productSpec == null)
            productSpecFinal = (productPath, criteriaBuilder) -> criteriaBuilder.isTrue(productPath.get("enable"));
        else {
            productSpecFinal = productSpec;
        }
        final BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpecFinal;
        if (depotSpec == null)
            depotSpecFinal = (depotJoin, criteriaBuilder) -> criteriaBuilder.isTrue(depotJoin.get("enable"));
        else {
            depotSpecFinal = depotSpec;
        }
        return usableStockInfo(productSpecFinal, depotSpecFinal);
    }

    @Override
    public StockInfoSet usableStock() {
        // 将来可能还会带去对 仓库和货品的规格要求
        return usableStockInfo(null, null);
    }

    @Override
    public StockInfoSet usableStockInfo(BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpec
            , BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpec) {
        if (productSpec == null)
            productSpec = (productPath, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (depotSpec == null)
            depotSpec = (depotJoin, criteriaBuilder) -> criteriaBuilder.conjunction();
        // 获取 该库存的最新结算量
        // 这里存在一个尴尬的情况
        // 如果存在结算库存 是否不符合规格也要返回 ？
        // 反之当前没有临时数据，那么就是拥有结算库存 也要忽略么？
        // 这2个结果都不符合需求
        StockSettlement settlement = lastStockSettlement(productSpec, depotSpec);
        // 获取 其后的和未结算的进出库订单合计
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // 这个不一样 是直接读取Number的
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        // 更理想化的数学结果是
        // 产品，入库，出库，数量
        // 1,1,null,100
        // 2,null,1,100

        Root<UnSettlementUsageStock> root = cq.from(UnSettlementUsageStock.class);
        Join<?, Product> productJoin = root.join("product");
        Join<?, Depot> destinationDepotJoin = root.join("destination", JoinType.LEFT);
        Join<?, Depot> originDepotJoin = root.join("origin", JoinType.LEFT);

        StockInfoSet resultList = new StockInfoSet();
        resultList.initAll(settlement.getUsableStock());

        entityManager.createQuery(
                cq.multiselect(productJoin, destinationDepotJoin, originDepotJoin, root.get("amount"))
                        .where(
                                cb.and(
                                        cb.or(
                                                destinationDepotJoin.isNull()
                                                , depotSpec.apply(destinationDepotJoin, cb)
                                        )
                                        , cb.or(
                                                originDepotJoin.isNull()
                                                , depotSpec.apply(originDepotJoin, cb)
                                        )
                                        , productSpec.apply(productJoin, cb)
                                )
                        )
        ).getResultList().forEach(
                tuple -> {
                    Product product = tuple.get(0, Product.class);
                    int amount = tuple.get(3, Integer.class);

                    Depot d1 = tuple.get(1, Depot.class);
                    if (d1 != null)
                        resultList.add(d1, product, amount);
                    Depot d2 = tuple.get(2, Depot.class);
                    if (d2 != null)
                        resultList.add(d2, product, -amount);
                }
        );

        CriteriaQuery<Product> pcq = cb.createQuery(Product.class);
        Root<Product> productRoot = pcq.from(Product.class);

        CriteriaQuery<Depot> dcq = cb.createQuery(Depot.class);
        Root<Depot> depotRoot = dcq.from(Depot.class);

        resultList.initAll(
                entityManager.createQuery(dcq.where(depotSpec.apply(depotRoot, cb))).getResultList()
                , entityManager.createQuery(pcq.where(productSpec.apply(productRoot, cb))).getResultList()
        );

        return resultList;
    }

    @Override
    public int usableStock(Depot depot, Product product) {
        // 获取 该库存的最新结算量
        StockSettlement settlement = lastStockSettlement();
        // 获取 其后的和未结算的进出库订单合计
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // 这个不一样 是直接读取Number的
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);

        Root<StockShiftUnit> root = cq.from(StockShiftUnit.class);
        MapJoin<StockShiftUnit, Product, ProductBatch> amountJoin = root.joinMap("amounts");
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

        Expression<Number> incoming = cb.sum(cb.prod(flag, amountJoin.value().get("amount")));
        return
                settlement.usableStock(depot, product) +
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

    @Override
    public void shiftEventUp(ShiftEvent event) {
        event.getUnit().addStatus(event.getTime(), event.getMessage(), event.getStatus(), event.getSource());
    }

    @Override
    public int usableStockTotal(Product product) {
        return enabledUsableStockInfo(((productPath, criteriaBuilder)
                -> criteriaBuilder.equal(productPath, product)), null)
                .forProduct(product)
                .stream().mapToInt(StockInfo::getAmount)
                .sum();
    }

    /**
     * @return 最新的结算信息;如果不存在会返回一个世纪前的一个结算点，结算量是0
     */
    private StockSettlement lastStockSettlement(BiFunction<Path<Product>, CriteriaBuilder, Predicate> productSpec
            , BiFunction<Path<Depot>, CriteriaBuilder, Predicate> depotSpec) {
        final LocalDateTime time = lastStockSettlementTime();
        if (time == null) {
            StockSettlement stockSettlement = new StockSettlement();
            stockSettlement.setUsableStock(Collections.emptySet());
            stockSettlement.setTime(LocalDateTime.now().minus(1, ChronoUnit.MILLENNIA));
            return stockSettlement;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockInfo> cq = cb.createQuery(StockInfo.class);
        Root<StockSettlement> root = cq.from(StockSettlement.class);
        SetJoin<StockSettlement, StockInfo> stockInfoSetJoin = root.joinSet("usableStock");

        StockSettlement stockSettlement = new StockSettlement();
        stockSettlement.setTime(time);
        stockSettlement.setUsableStock(new HashSet<>());
        stockSettlement.getUsableStock().addAll(entityManager.createQuery(
                cq
                        .select(stockInfoSetJoin
                        ).where(
                        cb.and(
                                cb.equal(root.get("time"), time)
                                , productSpec.apply(stockInfoSetJoin.get("product"), cb)
                                , depotSpec.apply(stockInfoSetJoin.join("depot"), cb)
                        )
                )
        ).getResultList());

        return stockSettlement;
    }

    /**
     * @return 最后的结算时间；没有就null
     */
    private LocalDateTime lastStockSettlementTime() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocalDateTime> cq = cb.createQuery(LocalDateTime.class);
        Root<StockSettlement> root = cq.from(StockSettlement.class);

        try {
            return entityManager.createQuery(
                    cq.select(cb.max(root.get("time").as(Number.class)).as(LocalDateTime.class))
            ).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * @return 最新的结算信息;如果不存在会返回一个世纪前的一个结算点，结算量是0
     */
    private StockSettlement lastStockSettlement() {
        StockSettlement settlement = stockSettlementRepository.findTop1ByOrderByTimeDesc();
        if (settlement == null) {
            settlement = new StockSettlement();
            settlement.setTime(LocalDateTime.now().minus(1, ChronoUnit.MILLENNIA));
        }
        return settlement;
    }

    @PostConstruct
    @Transactional
    public void init() throws SQLException {
        jdbcService.runJdbcWork(connection -> {
            String fileName;
            if (connection.profile().isMySQL()) {
                fileName = "mysql.sql";
            } else if (connection.profile().isH2()) {
                fileName = "h2.sql";
            } else
                throw new IllegalStateException("not support for:" + connection.getConnection());
            String resourceName = "/logistics_views/" + fileName;
            try {
                String code = StreamUtils.copyToString(new ClassPathResource(resourceName).getInputStream()
                        , Charset.forName("UTF-8"));
                try (Statement statement = connection.getConnection().createStatement()) {
                    statement.executeUpdate("DROP  TABLE IF EXISTS `UNSETTLEMENTUSAGESTOCK`");
                    statement.executeUpdate(code);
                }
            } catch (IOException e) {
                throw new IllegalStateException("读取SQL失败", e);
            }
        });
    }
}
