package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Depot;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author CJ
 */
public abstract class DepotRows implements RowDefinition<Depot> {

    //    private final LocalDateConverter localDateConverter = new LocalDateConverter();

    private final Function<LocalDateTime, String> orderTimeFormatter;

    public DepotRows(Function<LocalDateTime, String> orderTimeFormatter) {
        this.orderTimeFormatter = orderTimeFormatter;
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Depot> root) {
        return Arrays.asList(
                criteriaBuilder.asc(root.get("enable"))
                , criteriaBuilder.desc(root.get("createTime"))
        );
    }

    @Override
    public Class<Depot> entityClass() {
        return Depot.class;
    }

    @Override
    public List<FieldDefinition<Depot>> fields() {
        return Arrays.asList(
                Fields.asBasic("id")
                , Fields.asFunction("address", root -> root.get("address").get("otherAddress"))
                , Fields.asBasic("name")
                , Fields.asBasic("haierCode")
                , FieldBuilder.asName(Depot.class, "createTime")
                        .addFormat((data, type)
                                -> orderTimeFormatter.apply(((LocalDateTime) data)))
                        .build()
                , Fields.asBasic("enable")
        );
    }
}
