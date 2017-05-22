package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.service.ReadService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 展示订单数据的格式
 *
 * @author CJ
 */
public abstract class MainOrderRows implements RowDefinition<MainOrder> {

    //    private final LocalDateConverter localDateConverter = new LocalDateConverter();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA);

    @Override
    public Class<MainOrder> entityClass() {
        return MainOrder.class;
    }

    @Override
    public List<FieldDefinition<MainOrder>> fields() {
        return Arrays.asList(
                Fields.asBasic("id")
                , Fields.asBiFunction("orderId", MainOrder::getSerialId)
                , Fields.asBiFunction("orderUser", ((root, criteriaBuilder)
                        -> ReadService.nameForLogin(MainOrder.getCustomerLogin(root)
                        , criteriaBuilder)))
                , Fields.asBiFunction("phone", (root, criteriaBuilder)
                        -> ReadService.mobileForLogin(MainOrder.getCustomerLogin(root), criteriaBuilder))
                , Fields.asFunction("category", root -> root.get("good").get("product").get("name"))
                , Fields.asFunction("type", root -> root.get("good").get("product").get("code"))
                , Fields.asBasic("amount")
                , Fields.asBiFunction("package", ((root, criteriaBuilder)
                        -> criteriaBuilder.literal("")))
                , Fields.asBiFunction("method", ((root, criteriaBuilder)
                        -> criteriaBuilder.literal("")))
                , Fields.asBiFunction("total", (MainOrder::getOrderDueAmount))
                , Fields.asFunction("address", root -> root.get("installAddress").get("otherAddress"))
                , FieldBuilder.asName(MainOrder.class, "status")
                        .addSelect(root -> root.get("orderStatus"))
                        .addFormat((data, type) -> data == null ? null : data.toString())
                        .build()
                , FieldBuilder.asName(MainOrder.class, "statusCode")
                        .addSelect(root -> root.get("orderStatus"))
                        .addFormat((data, type) -> data == null ? null : ((Enum) data).ordinal())
                        .build()
                , FieldBuilder.asName(MainOrder.class, "orderTime")
                        .addFormat((data, type)
                                -> ((LocalDateTime) data).format(formatter))
                        .build()
        );
    }
}
