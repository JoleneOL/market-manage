package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Customer_;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.rows.StockShiftUnitRows;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.entity.Product_;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_LOGISTICS + "','" + Login.ROLE_SUPPLY_CHAIN + "')")
public class ManageOrderController {

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private ApplicationContext applicationContext;
    private ConversionService conversionService;
    @Autowired
    private LogisticsService logisticsService;

    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_LOGISTICS + "','" + Login.ROLE_SUPPLY_CHAIN + "','" + Login.ROLE_LOOK + "')")
    @GetMapping("/manage/orderData/logistics")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainOrder> logisticsData(@AuthenticationPrincipal Login login, String mobile, Long depotId
            , String productCode
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate orderDate) {
        if (conversionService == null) {
            conversionService = applicationContext.getBean(ConversionService.class);
        }
        return new MainOrderRows(login, time -> conversionService.convert(time, String.class)) {

            private ListJoin<MainOrder, StockShiftUnit> unitJoin;

            @Override
            public Expression<?> count(CriteriaQuery<Long> countQuery, CriteriaBuilder criteriaBuilder, Root<MainOrder> root) {
                return unitJoin;
            }

            @Override
            public List<FieldDefinition<MainOrder>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(MainOrder.class, "mainOrderId")
                                .addSelect(root -> root.get("id"))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "id")
                                .addSelect(root -> {
                                    unitJoin = root.join(MainOrder_.logisticsSet);
                                    return unitJoin.get("id");
                                })
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "supplierId")
                                .addBiSelect((root, criteriaBuilder)
                                        -> StockShiftUnitRows.getSupplierId(unitJoin, criteriaBuilder))
                                .build()
                        , Fields.asBiFunction("orderId", MainOrder::getSerialId)
                        , getOrderTime()
                        , FieldBuilder.asName(MainOrder.class, "address")
                                .addSelect(root -> root.get("installAddress"))
                                .addFormat((object, type) -> object.toString())
                                .build()
                        , Fields.asFunction("orderUser", ((root)
                                -> root.get(MainOrder_.customer).get(Customer_.name)))
                        , Fields.asBiFunction("mobile", (root, criteriaBuilder)
                                -> Customer.getMobile(MainOrder.getCustomer(root)))

                        , FieldBuilder.asName(MainOrder.class, "storage")
                                .addSelect(root -> StockShiftUnit.originJoin(unitJoin).get("name"))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "deliverTime")
                                .addSelect(stockShiftUnitRoot -> unitJoin.get("createTime"))
                                .addFormat((data, type) -> localDateTimeFormatter.apply((LocalDateTime) data))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "status")
                                .addBiSelect((root, criteriaBuilder) -> unitJoin.get("currentStatus"))
                                .addFormat((obj, type) -> obj.toString())
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "stateCode")
                                .addBiSelect((root, criteriaBuilder) -> unitJoin.get("currentStatus"))
                                .addFormat((obj, type) -> ((Enum) obj).ordinal())
                                .build()
                );
            }

            @Override
            public Specification<MainOrder> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.equal(unitJoin.type(), HaierOrder.class);
                    if (!StringUtils.isEmpty(mobile))
                        predicate = cb.and(predicate, cb.like(Customer.getMobile(MainOrder.getCustomer(root))
                                , "%" + mobile + "%"));
                    if (depotId != null) {
                        predicate = cb.and(predicate, cb.equal(unitJoin.get("origin").get("id"), depotId));
                    }
                    if (!StringUtils.isEmpty(productCode)) {
                        root.fetch(MainOrder_.amounts);
                        predicate = cb.and(predicate
                                , cb.equal(root.join(MainOrder_.amounts).key().get(MainGood_.product).get(Product_.code)
                                        , productCode));
                    }
                    if (orderDate != null) {
                        predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get("orderTime"), orderDate));
                    }
                    return predicate;
                };
            }
        };
    }

    @GetMapping("/orderData/logistics/{orderId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public Object preLogistics(@PathVariable("orderId") long orderId) {
        Map<String, Object> data = new HashMap<>();
//        MainOrder order = mainOrderService.getOrder(orderId);
        data.put("depots", mainOrderService.depotsForOrder(orderId).stream()
//                .filter(stockInfo -> stockInfo.getAmount() >= order.getAmount())
                        // 库存多的优先
//                .sorted((o1, o2) -> o2.getAmount() - o1.getAmount())
                        .map(info -> {
                            Map<String, Object> x = new HashMap<>();
                            x.put("id", info.getId());
                            x.put("name", info.getName());
                            x.put("quantity", 99999);
                            x.put("distance", -1);
                            return x;
                        })
                        .collect(Collectors.toSet())
        );
        return data;
    }

    @GetMapping("/mainOrderDelivery")
    @Transactional(readOnly = true)
    public String mainOrderDelivery(long id, Model model) {
        // 物流模块提供数据，视图层依然由客户端项目，期待将来更高级视图解决方案的设计
        MainOrder order = mainOrderService.getOrder(id);
        logisticsService.viewModelForDelivery(order, model);
        model.addAttribute("parentPageUri", "/orderManage");
        model.addAttribute("parentPageName", "订单管理");
        return "_orderDelivery.html";
    }

    @GetMapping("/mainOrderDetail")
    @Transactional(readOnly = true)
    public String orderDetail(long id, Model model) {
        final MainOrder order = mainOrderService.getOrder(id);
        model.addAttribute("currentData", order);
        model.addAttribute("shipList", order.getLogisticsSet()
                .stream()
                .sorted(Comparator.comparing(StockShiftUnit::getCreateTime))
                .collect(Collectors.toList()));
        return "_orderDetail.html";
    }

}
