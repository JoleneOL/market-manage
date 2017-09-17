package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.Tag_;
import cn.lmjia.market.core.row.FieldDefinition;
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
 * Created by helloztt on 2017/9/16.
 */
public abstract class TagRows extends AbstractRows<Tag> {
    public TagRows(Function<LocalDateTime, String> localDateTimeFormatter) {
        super(localDateTimeFormatter);
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Tag> root) {
        return Arrays.asList(
                criteriaBuilder.desc(root.get(Tag_.weight)),
                criteriaBuilder.asc(root.get(Tag_.disabled))
        );
    }

    @Override
    public Class<Tag> entityClass() {
        return Tag.class;
    }

    @Override
    public List<FieldDefinition<Tag>> fields() {
        return Arrays.asList(
                Fields.asBasic("name"),
                FieldBuilder.asName(Tag.class, "type")
                        .addSelect(root -> root.get(Tag_.type))
                        .addFormat((object, type) -> object.toString())
                        .build(),
                Fields.asBasic("weight"),
                FieldBuilder.asName(Tag.class, "disabled")
                        .addSelect(root -> root.get(Tag_.disabled))
                        .addFormat((data, type) -> {
                            boolean disabled = (boolean) data;
                            if (disabled) {
                                return "禁用";
                            } else {
                                return "启用";
                            }
                        })
                        .build()
        );
    }
}
