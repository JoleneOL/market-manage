package cn.lmjia.market.core.rows;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.haier.entity.HaierOrder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
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
public abstract class StockShiftUnitRows extends AbstractRows<StockShiftUnit> {

    public StockShiftUnitRows(Function<LocalDateTime, String> orderTimeFormatter) {
        super(orderTimeFormatter);
    }

    public static Expression<?> getSupplierId(Root<StockShiftUnit> stockShiftUnitRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.selectCase(stockShiftUnitRoot.get("classType"))
                .when("HaierOrder", criteriaBuilder.treat(stockShiftUnitRoot, HaierOrder.class).get("orderNumber"))
                .otherwise("未知");
    }

    public static Expression<?> getSupplierId(Join<?, StockShiftUnit> stockShiftUnitRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.selectCase(stockShiftUnitRoot.get("classType"))
                .when("HaierOrder", criteriaBuilder.treat(stockShiftUnitRoot, HaierOrder.class).get("orderNumber"))
                .otherwise("未知");
    }

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
                        .addFormat((data, type) -> localDateTimeFormatter.apply((LocalDateTime) data))
                        .build()
                , FieldBuilder.asName(StockShiftUnit.class, "supplierId")
                        .addBiSelect(StockShiftUnitRows::getSupplierId)
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

}
