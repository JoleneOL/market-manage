package cn.lmjia.market.core.row.field;

import cn.lmjia.market.core.row.FieldDefinition;
import org.springframework.http.MediaType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.function.Function;

/**
 * 基本字段定义，仅根据表达式(Expression)
 *
 * @author CJ
 */
public class BasicExpressionField implements FieldDefinition {

    private final Function<Root<?>, Expression<?>> toExpression;
    private final String name;

    public BasicExpressionField(String name, Function<Root<?>, Expression<?>> toExpression) {
        this.name = name;
        this.toExpression = toExpression;
    }

    @Override
    public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<?> root) {
        return toExpression.apply(root);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
        return origin;
    }

    @Override
    public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return toExpression.apply(root);
    }
}
