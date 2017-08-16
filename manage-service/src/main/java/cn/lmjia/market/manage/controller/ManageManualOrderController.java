package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.order.ManualOrder;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.logistics.entity.UsageStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 手动发货控制器
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageManualOrderController {

    @Autowired
    private ConversionService conversionService;

    @GetMapping("/storage/transfer")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<UsageStock> useAbleStock(String product, int amount) {
        return new RowDefinition<UsageStock>() {
            @Override
            public Class<UsageStock> entityClass() {
                return UsageStock.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<UsageStock> root) {
                return Collections.singletonList(criteriaBuilder.desc(root.get("amount")));
            }

            @Override
            public List<FieldDefinition<UsageStock>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(UsageStock.class, "id")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("id"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "storage")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("name"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "quantity")
                                .addSelect(usageStockRoot -> usageStockRoot.get("amount"))
                                .build()
                );
            }

            @Override
            public Specification<UsageStock> specification() {
                return (root, query, cb) -> cb.and(
                        cb.equal(root.get("product").get("code"), product)
                        , cb.greaterThanOrEqualTo(root.get("amount"), amount)
                );
            }
        };
    }

    @GetMapping("/storage/transfer")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<ManualOrder> list(String orderId, String phone
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate orderDate) {
        return new RowDefinition<ManualOrder>() {
            @Override
            public Class<ManualOrder> entityClass() {
                return ManualOrder.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<ManualOrder> root) {
                return Collections.singletonList(
                        criteriaBuilder.desc(root.get("createdTime"))
                );
            }

            @Override
            public List<FieldDefinition<ManualOrder>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(ManualOrder.class, "productName")
                                .addSelect(manualOrderRoot -> manualOrderRoot.get("product").get("name"))
                                .build()
                        , FieldBuilder.asName(ManualOrder.class, "productCode")
                                .addSelect(manualOrderRoot -> manualOrderRoot.get("product").get("code"))
                                .build()
                        , Fields.asBasic("amount")
                        , Fields.asBasic("price")
                        , Fields.asBasic("mobile")
                        , FieldBuilder.asName(ManualOrder.class, "createdTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(ManualOrder.class, "address")
                                .addFormat((data, type) -> data.toString())
                                .build()
                        , FieldBuilder.asName(ManualOrder.class, "status")
                                .addSelect(manualOrderRoot -> manualOrderRoot.get("shiftUnit").get("currentStatus"))
                                .addFormat((data, type) -> data.toString())
                                .build()
                        , FieldBuilder.asName(ManualOrder.class, "unitId")
                                .addSelect(manualOrderRoot -> manualOrderRoot.get("shiftUnit").get("code"))
                                .build()
                );
            }

            @Override
            public Specification<ManualOrder> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.conjunction();
                    if (!StringUtils.isEmpty(orderId)) {
                        try {
                            predicate = cb.and(predicate, cb.equal(root.get("id"), NumberUtils.parseNumber(orderId, Long.class)));
                        } catch (NumberFormatException ignored) {

                        }
                    }
                    if (!StringUtils.isEmpty(phone))
                        predicate = cb.and(predicate, cb.like(root.get("mobile"), "%" + phone + "%"));
                    if (orderDate != null)
                        predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get("createdTime"), orderDate));
                    return predicate;
                };
            }
        };
    }

}
