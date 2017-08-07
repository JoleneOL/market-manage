package cn.lmjia.market.manage.controller.logistics;

import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductBatch;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import me.jiangcai.logistics.repository.StockShiftUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageLogisticsController {

    @Autowired
    private StockShiftUnitRepository stockShiftUnitRepository;
    @Autowired
    private ConversionService conversionService;

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
        return new RowDefinition<StockShiftUnit>() {
            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<StockShiftUnit> root) {
                return Collections.singletonList(criteriaBuilder.desc(root.get("createTime")));
            }

            @Override
            public Class<StockShiftUnit> entityClass() {
                return StockShiftUnit.class;
            }

            @Override
            public List<FieldDefinition<StockShiftUnit>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(StockShiftUnit.class, "orderTime")
                                .addSelect(stockShiftUnitRoot -> stockShiftUnitRoot.get("createTime"))
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "supplierId")
                                .addBiSelect((stockShiftUnitRoot, criteriaBuilder) -> {
                                    return criteriaBuilder.selectCase(stockShiftUnitRoot.get("classType"))
                                            .when("HaierOrder", criteriaBuilder.treat(stockShiftUnitRoot, HaierOrder.class).get("orderNumber"))
                                            .otherwise("未知");
                                })
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "depotName")
                                .addSelect(stockShiftUnitRoot -> StockShiftUnit.destinationJoin(stockShiftUnitRoot).get("name"))
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "address")
                                .addSelect(stockShiftUnitRoot -> StockShiftUnit.destinationJoin(stockShiftUnitRoot).get("address"))
                                .addFormat((data, type) -> data.toString())
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "contacts")
                                .addSelect(stockShiftUnitRoot -> Depot.name(StockShiftUnit.destinationJoin(stockShiftUnitRoot)))
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "mobile")
                                .addSelect(stockShiftUnitRoot -> Depot.mobile(StockShiftUnit.destinationJoin(stockShiftUnitRoot)))
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "status")
                                .addSelect(stockShiftUnitRoot -> stockShiftUnitRoot.get("currentStatus"))
                                .addFormat((object, type) -> object.toString())
                                .build()
                        , FieldBuilder.asName(StockShiftUnit.class, "stateCode")
                                .addSelect(stockShiftUnitRoot -> stockShiftUnitRoot.get("currentStatus"))
                                .addFormat((object, type) -> ((Enum) object).ordinal())
                                .build()
                );
            }

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
