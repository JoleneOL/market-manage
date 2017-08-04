package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.haier.entity.HaierDepot;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
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
                , FieldBuilder.asName(Depot.class, "type")
                        .addSelect(Path::type)
                        .addFormat((data, type) -> {
                            Class clazz = (Class) data;
                            if (clazz == HaierDepot.class)
                                return "日日顺";
                            return "普通";
                        })
                        .build()
                , FieldBuilder.asName(Depot.class, "address")
                        .addSelect(root -> root.get("address"))
                        .addFormat((object, type) -> object.toString())
                        .build()
                , Fields.asBasic("name")
                , Fields.asBasic("chargePeopleName")
                , Fields.asBasic("chargePeopleMobile")
                , FieldBuilder.asName(Depot.class, "supplierInfo")
                        .addBiSelect(((depotRoot, criteriaBuilder) -> {
                            Expression<String> other = criteriaBuilder.literal("无");
                            Expression<String> haier = criteriaBuilder.concat("编码："
//                                    , criteriaBuilder.literal("1")
                                    , criteriaBuilder.treat(depotRoot, HaierDepot.class).get("haierCode")
                            );
                            return criteriaBuilder.selectCase(depotRoot.get("classType").as(String.class))
                                    .when("HaierDepot", haier)
                                    .otherwise(other);
                        }))
                        .build()
                , FieldBuilder.asName(Depot.class, "createTime")
                        .addFormat((data, type)
                                -> orderTimeFormatter.apply(((LocalDateTime) data)))
                        .build()
                , Fields.asBasic("enable")
        );
    }
}
