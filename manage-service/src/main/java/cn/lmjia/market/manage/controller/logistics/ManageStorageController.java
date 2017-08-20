package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.entity.Factory;
import cn.lmjia.market.core.repository.FactoryRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.Deliverable;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.StockInfoSet;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.UsageStock;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.StockInfo;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT')")
public class ManageStorageController {

    @Autowired
    private StockService stockService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private ReadService readService;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private HaierSupplier haierSupplier;

    @PostMapping("/manage/addStockAsRoot")
    @Transactional
    public String addStockAsRoot(long depotId, String productCode, int amount, String message) {
        stockService.addStock(depotRepository.getOne(depotId), productRepository.getOne(productCode), amount, message);
        return "redirect:/manageStorage";
    }

    @GetMapping("/manage/storage")
    @Transactional(readOnly = true)
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<UsageStock> data(Long depotId, String productCode) {
        return new RowDefinition<UsageStock>() {
            @Override
            public Class<UsageStock> entityClass() {
                return UsageStock.class;
            }

            @Override
            public List<FieldDefinition<UsageStock>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(UsageStock.class, "storageType")
                                .addBiSelect((usageStockRoot, criteriaBuilder)
                                        -> criteriaBuilder.selectCase(usageStockRoot.get("depot").get("classType"))
                                        .when("HaierDepot", "日日顺")
                                        .otherwise("普通"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "storage")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("name"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "depotId")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("id"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "product")
                                .addSelect(usageStockRoot -> usageStockRoot.get("product").get("name"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "productCode")
                                .addSelect(usageStockRoot -> usageStockRoot.get("product").get("code"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "inventory")
                                .addSelect(usageStockRoot -> usageStockRoot.get("amount"))
                                .build()
                );
            }

            @Override
            public Specification<UsageStock> specification() {
                return (root, query, cb) -> {
                    final Predicate predicate = cb.and(
                            depotId == null ? cb.isTrue(root.get("depot").get("enable"))
                                    : cb.equal(root.get("depot").get("id"), depotId)
                            , StringUtils.isEmpty(productCode) ? cb.isTrue(root.get("product").get("enable"))
                                    : cb.equal(root.get("product").get("code"), productCode)
                    );
                    if (StringUtils.isEmpty(productCode) && depotId == null)
                        return cb.and(predicate, cb.greaterThan(root.get("amount"), 0));
                    return predicate;
                };
            }
        };
    }

    // 所有库存信息
//    @GetMapping("/manage/storage")
//    @ResponseBody
//    @Transactional(readOnly = true)
    public Object data2(Integer draw, Long depotId, String productCode) {
        StockInfoSet set = stockService.enabledUsableStockInfo(
                StringUtils.isEmpty(productCode) ? null
                        : (BiFunction<Path<Product>, CriteriaBuilder, Predicate>) (productPath, criteriaBuilder)
                        -> criteriaBuilder.equal(productPath.get("code"), productCode)
                , depotId == null ? null
                        : (BiFunction<Path<Depot>, CriteriaBuilder, Predicate>) (depotPath, criteriaBuilder)
                        -> criteriaBuilder.equal(depotPath.get("id"), depotId));
        Set<StockInfo> stockInfoSet = set.forAll();
        Map<String, Object> data = new HashMap<>();
        data.put("draw", draw == null ? 1 : draw);
        data.put("recordsTotal", stockInfoSet.size());
        data.put("recordsFiltered", stockInfoSet.size());
        data.put("data", stockInfoSet.stream()
                .map(stockInfo -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("storageType", (stockInfo.getDepot() instanceof HaierDepot) ? "日日顺" : "普通");
                    info.put("storage", stockInfo.getDepot().getName());
                    info.put("depotId", stockInfo.getDepot().getId());
                    info.put("product", stockInfo.getProduct().getName());
                    info.put("productCode", stockInfo.getProduct().getCode());
                    info.put("inventory", stockInfo.getAmount());
                    return info;
                })
                .collect(Collectors.toList())
        );

        return data;
    }

    @GetMapping("/manageStorage")
    @Transactional(readOnly = true)
    public String index(Model model) {
        StockInfoSet set = stockService.enabledUsableStock();
        int boundary = systemStringService.getCustomSystemString("market.key.stock.warning.boundary"
                , "market.key.stock.warning.boundary.comment", true, Integer.class, 50);
        // 一种无货的
        model.addAttribute("emptyList", set.forAll().stream()
                .filter(stockInfo -> stockInfo.getAmount() <= 0)
                .limit(4)
                .collect(Collectors.toList()));
        // 一种是缺货的
        model.addAttribute("warnList", set.forAll().stream()
                .filter(stockInfo -> stockInfo.getAmount() > 0 && stockInfo.getAmount() < boundary)
                .sorted(Comparator.comparingInt(StockInfo::getAmount))
                .limit(4)
                .collect(Collectors.toList()));
        // 按照货品 取出所有库存
        Set<Product> products = set.forAll().stream()
                .map(StockInfo::getProduct).collect(Collectors.toSet());
        Set<Map<String, Object>> amounts = new HashSet<>();
        products.forEach(product -> {
            Map<String, Object> data = new HashMap<>();
            data.put("name", product.getName());
            data.put("value", set.forProduct(product).stream()
                    .mapToInt(StockInfo::getAmount)
                    .sum());
            amounts.add(data);
        });
        model.addAttribute("allProduct", products.stream().map(Product::getName).collect(Collectors.toSet()));
        model.addAttribute("allProductAmount", amounts);

        return "_storageManage.html";
    }

    @PostMapping("/manageStorageDelivery")
    @Transactional
    public String sendToDepot(long factory, String product, long depot, int deliverQuantity) {
        // 目前只支持日日顺
        Depot depotInfo = depotRepository.getOne(depot);
        if (!(depotInfo instanceof HaierDepot))
            throw new IllegalStateException("目前仅支持日日顺仓库");
        Factory factoryInfo = factoryRepository.getOne(factory);
        Product productInfo = productRepository.getOne(product);
        //
        logisticsService.makeShift(haierSupplier, Collections.singleton(new Thing() {
            @Override
            public Product getProduct() {
                return productInfo;
            }

            @Override
            public ProductStatus getProductStatus() {
                return ProductStatus.normal;
            }

            @Override
            public int getAmount() {
                return deliverQuantity;
            }
        }), factoryInfo, depotInfo);

        return "redirect:/manageLogistics#factory";
    }

    @GetMapping("/manageStorageDelivery")
    @Transactional(readOnly = true)
    public String delivery(Long depotId, String productCode, Model model) {
        final List<Depot> depotList = readService.allEnabledDepot();
        model.addAttribute("depotList", depotList);
        if (depotId != null) {
            Depot prefectDepot = depotRepository.getOne(depotId);
            if (!depotList.contains(prefectDepot))
                depotList.add(prefectDepot);
            model.addAttribute("prefectDepot", prefectDepot);
        }
        final List<Product> productList = readService.allEnabledProduct();
        model.addAttribute("productList", productList);
        if (!StringUtils.isEmpty(productCode)) {
            Product prefectProduct = productRepository.getOne(productCode);
            if (!productList.contains(prefectProduct)) {
                productList.add(prefectProduct);
            }
            model.addAttribute("prefectProduct", prefectProduct);
        }
        final List<Factory> factoryList = factoryRepository.findByEnableTrue();
        model.addAttribute("factoryList", factoryList);

        // 转出 depotCS 和 factoryCS
        model.addAttribute("depotCS", depotList.stream().collect(Collectors.toMap(depot
                -> String.valueOf(depot.getId()), this::toMap)));
        model.addAttribute("factoryCS", factoryList.stream().collect(Collectors.toMap(depot
                -> String.valueOf(depot.getId()), this::toMap)));

        return "_delivery.html";
    }

    private Map<String, String> toMap(Deliverable deliverable) {
        Map<String, String> data = new HashMap<>();
        data.put("address", deliverable.getProvince() + "-" + deliverable.getCity()
                + "-" + deliverable.getCountry() + "-" + deliverable.getDetailAddress());
        data.put("name", deliverable.getConsigneeName());
        data.put("mobile", deliverable.getConsigneeMobile());
        return data;
    }

}
