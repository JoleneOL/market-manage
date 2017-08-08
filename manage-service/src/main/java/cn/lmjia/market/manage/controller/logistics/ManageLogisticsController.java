package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.StockShiftUnitRows;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductBatch;
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

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageLogisticsController {

    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/manageLogistics")
    public String index() {
        return "_logisticsManage.html";
    }

    @GetMapping("/manageShiftDetail")
    public String detail(Model model, long id) {
        model.addAttribute("currentData", stockShiftUnitRepository.getOne(id));
        return "_logisticsDetail.html";
    }

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
                                , JpaFunctionUtils.DateEqual(cb, StockShiftUnit.createDate(root), orderDate));
                    }
                    return predicate;
                };
            }
        };
    }

}
