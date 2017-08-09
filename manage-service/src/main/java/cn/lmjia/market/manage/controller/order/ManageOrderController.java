package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.rows.StockShiftUnitRows;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.haier.HaierSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageOrderController {

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private ConversionService conversionService;

    @GetMapping("/manage/orderData/logistics")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainOrder> logisticsData(@AuthenticationPrincipal Login login) {
        return new MainOrderRows(login, time -> conversionService.convert(time, String.class)) {

            @Override
            public List<FieldDefinition<MainOrder>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(MainOrder.class, "unitId")
                                .addSelect(root -> root.get("currentLogistics").get("id"))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "supplierId")
                                .addBiSelect((root, criteriaBuilder)
                                        -> StockShiftUnitRows.getSupplierId(root.join("currentLogistics"), criteriaBuilder))
                                .build()
                        , Fields.asBiFunction("orderId", MainOrder::getSerialId)
                        , Fields.asFunction("goods", root -> root.get("good").get("product").get("name"))
                        , Fields.asBasic("amount")
                        , getOrderTime()
                        , FieldBuilder.asName(MainOrder.class, "address")
                                .addSelect(root -> root.get("installAddress"))
                                .addFormat((object, type) -> object.toString())
                                .build()
                        , Fields.asBiFunction("orderUser", ((root, criteriaBuilder)
                                -> ReadService.nameForLogin(MainOrder.getCustomerLogin(root)
                                , criteriaBuilder)))
                        , Fields.asBiFunction("mobile", (root, criteriaBuilder)
                                -> Customer.getMobile(MainOrder.getCustomer(root)))

                        , FieldBuilder.asName(MainOrder.class, "storage")
                                .addSelect(root -> StockShiftUnit.originJoin(root.join("currentLogistics")).get("name"))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "deliverTime")
                                .addSelect(stockShiftUnitRoot -> stockShiftUnitRoot.get("currentLogistics").get("createTime"))
                                .addFormat((data, type) -> localDateTimeFormatter.apply((LocalDateTime) data))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "status")
                                .addBiSelect((root, criteriaBuilder) -> root.get("currentLogistics").get("currentStatus"))
                                .addFormat((obj, type) -> obj.toString())
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "stateCode")
                                .addBiSelect((root, criteriaBuilder) -> root.get("currentLogistics").get("currentStatus"))
                                .addFormat((obj, type) -> ((Enum) obj).ordinal())
                                .build()
                );
            }

            @Override
            public Specification<MainOrder> specification() {
                return null;
            }
        };
    }

    @GetMapping("/orderData/logistics/{orderId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public Object preLogistics(@PathVariable("orderId") long orderId) {
        Map<String, Object> data = new HashMap<>();
        MainOrder order = mainOrderService.getOrder(orderId);
        data.put("depots", mainOrderService.depotsForOrder(orderId).stream()
                .filter(stockInfo -> stockInfo.getAmount() >= order.getAmount())
                // 库存多的优先
                .sorted((o1, o2) -> o2.getAmount() - o1.getAmount())
                .map(info -> {
                    Map<String, Object> x = new HashMap<>();
                    x.put("id", info.getDepot().getId());
                    x.put("name", info.getDepot().getName());
                    x.put("quantity", info.getAmount());
                    x.put("distance", -1);
                    return x;
                })
                .collect(Collectors.toSet())
        );
        return data;
    }

    @PutMapping("/orderData/logistics/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeLogistics(@PathVariable("orderId") long orderId, @RequestBody String depotId) {
        mainOrderService.makeLogistics(HaierSupplier.class, orderId, NumberUtils.parseNumber(depotId, Long.class));
    }

}
