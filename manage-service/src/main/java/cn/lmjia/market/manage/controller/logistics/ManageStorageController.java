package cn.lmjia.market.manage.controller.logistics;

import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.logistics.StockInfoSet;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.support.StockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
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
    public Object data() {
        return null;
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
