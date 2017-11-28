package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Customer_;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder_;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author CJ
 */
public abstract class AbstractMainDeliverableOrderRows<T extends MainDeliverableOrder> extends AbstractRows<T> {

    AbstractMainDeliverableOrderRows(Function<LocalDateTime, String> localDateTimeFormatter) {
        super(localDateTimeFormatter);
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return Collections.singletonList(criteriaBuilder.desc(root.get(MainDeliverableOrder_.orderTime)));
    }


    protected FieldDefinition<T> getOrderTime() {
        return FieldBuilder.asName(entityClass(), "orderTime")
                .addFormat((data, type)
                        -> localDateTimeFormatter.apply(((LocalDateTime) data)))
                .build();
    }

    @Override
    public List<FieldDefinition<T>> fields() {
        return Arrays.asList(
                Fields.asBasic("id")
                , Fields.asBasic("orderBody")
                , FieldBuilder.asName(entityClass(), "orderUser")
                        .addSelect(root -> root.get(MainDeliverableOrder_.customer).get(Customer_.name))
                        .build()
                , Fields.asFunction("orderUser", ((root)
                        -> root.get(MainDeliverableOrder_.customer).get(Customer_.name)))
                , Fields.asBiFunction("phone", (root, criteriaBuilder)
                        -> Customer.getMobile(root.get(MainDeliverableOrder_.customer)))
                , Fields.asBiFunction("package", ((root, criteriaBuilder)
                        -> criteriaBuilder.literal("")))
                , FieldBuilder.asName(entityClass(), "total")
                        .addSelect(root -> root.get(MainDeliverableOrder_.goodTotalPriceAmountIndependent))
                        .build()
                , FieldBuilder.asName(entityClass(), "address")
                        .addSelect(root -> root.get(MainDeliverableOrder_.installAddress))
                        .addFormat((object, type) -> object.toString())
                        .build()
                , FieldBuilder.asName(entityClass(), "status")
                        .addSelect(root -> root.get(MainDeliverableOrder_.orderStatus))
                        .addFormat((data, type) -> data == null ? null : data.toString())
                        .build()
                , FieldBuilder.asName(entityClass(), "statusCode")
                        .addSelect(root -> root.get(MainDeliverableOrder_.orderStatus))
                        .addFormat((data, type) -> data == null ? null : ((Enum) data).ordinal())
                        .build()
                , getOrderTime()
//                , FieldBuilder.asName(MainOrder.class, "quickDoneAble")
//                        .addSelect(root -> root.get("orderStatus"))
//                        .addFormat((data, type) -> {
//                            OrderStatus orderStatus = (OrderStatus) data;
//                            return login.isManageable() && orderStatus == OrderStatus.forDeliver;
//                        })
//                        .build()
        );
    }
}
