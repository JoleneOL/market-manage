package cn.lmjia.market.manage.controller.logistics;

import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.StockInfoSet;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.StockInfo;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Comparator;
import java.util.HashMap;
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

    // 库存 为0 的信息
    // 库存 小于警戒线的信息
    // 每一种货品的 所有库存信息
    @Autowired
    private StockService stockService;
    @Autowired
    private SystemStringService systemStringService;

    // 所有库存信息
    @GetMapping("/manage/storage")
    @ResponseBody
    @Transactional(readOnly = true)
    public Object data(Long depotId, String productCode) {
        StockInfoSet set = stockService.enabledUsableStockInfo(
                StringUtils.isEmpty(productCode) ? null
                        : (BiFunction<Path<Product>, CriteriaBuilder, Predicate>) (productPath, criteriaBuilder)
                        -> criteriaBuilder.equal(productPath.get("code"), productCode)
                , depotId == null ? null
                        : (BiFunction<Path<Depot>, CriteriaBuilder, Predicate>) (depotPath, criteriaBuilder)
                        -> criteriaBuilder.equal(depotPath.get("id"), depotId));
        Set<StockInfo> stockInfoSet = set.forAll();
        Map<String, Object> data = new HashMap<>();
        data.put("draw", 1);
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


        return "_storageManage.html";
    }

    @GetMapping("/manageStorageDelivery")
    public String delivery() {
        return "_delivery.html";
    }

}
