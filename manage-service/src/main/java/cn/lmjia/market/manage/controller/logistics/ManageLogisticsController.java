package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.rows.StockShiftUnitRows;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.ManuallyOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * 管理物流,只有root权限,供应链管理权限,物流管理权限可以操作.
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_LOGISTICS + "','" + Login.ROLE_SUPPLY_CHAIN + "')")
public class ManageLogisticsController {

    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"','" + Login.ROLE_LOGISTICS + "')")
    @GetMapping("/manageLogistics")
    public String index() {
        return "_logisticsManage.html";
    }

    @GetMapping("/manageShiftDetail")
    public String detail(Model model, long id) {
        final StockShiftUnit shiftUnit = stockShiftUnitRepository.getOne(id);
        model.addAttribute("currentData", shiftUnit);
        if (shiftUnit.getCurrentStatus() != ShiftStatus.success
                && shiftUnit.getCurrentStatus() != ShiftStatus.reject) {
            model.addAttribute("allowEvent", true);
        } else
            model.addAttribute("allowEvent", false);
        // && shiftUnit.isInstallation()  为了便于管理员发起安装事件 这里不再计较是否是一个安装订单
        if (shiftUnit.getCurrentStatus() == ShiftStatus.success) {
            model.addAttribute("allowInstallEvent", true);
        } else
            model.addAttribute("allowInstallEvent", false);

        if (shiftUnit instanceof HaierOrder)
            model.addAttribute("haierOrder", shiftUnit);
        if (shiftUnit instanceof ManuallyOrder)
            model.addAttribute("manuallyOrder", shiftUnit);
        model.addAttribute("events", shiftUnit.getEvents().values().stream()
                .sorted((o1, o2) -> o2.getTime().compareTo(o1.getTime()))
                .collect(Collectors.toList()));
        return "_logisticsDetail.html";
    }

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manage/factoryOut")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<StockShiftUnit> factoryOut(String mobile, Long depotId, String productCode
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate orderDate) {
        // 入库订单 应该按照时间排序吧
        // status
        return new StockShiftUnitRows(localDateTime
                -> applicationContext.getBean(ConversionService.class).convert(localDateTime, String.class)) {
            @Override
            public Specification<StockShiftUnit> specification() {
                return (root, query, cb) -> {
                    final Join<StockShiftUnit, Depot> stockShiftUnitDepotJoin = StockShiftUnit.destinationJoin(root);
                    Predicate predicate = cb.and(
                            stockShiftUnitDepotJoin.isNotNull(),
                            StockShiftUnit.originJoin(root).isNull()
                    );
                    if (!StringUtils.isEmpty(mobile))
                        predicate = cb.and(predicate, cb.like(Depot.mobile(stockShiftUnitDepotJoin)
                                , "%" + mobile + "%"));
                    if (depotId != null)
                        predicate = cb.and(predicate, cb.equal(stockShiftUnitDepotJoin.get("id"), depotId));
                    // productCode 包含这个商品
                    if (!StringUtils.isEmpty(productCode)) {
                        MapJoin<StockShiftUnit, Product, ProductBatch> amountJoin = root.joinMap("amounts");
                        predicate = cb.and(predicate, cb.equal(amountJoin.key().get("code"), productCode));
                    }
                    if (orderDate != null) {
                        predicate = cb.and(
                                predicate
                                , JpaFunctionUtils.dateEqual(cb, StockShiftUnit.createDate(root), orderDate));
                    }
                    return predicate;
                };
            }
        };
    }

}
