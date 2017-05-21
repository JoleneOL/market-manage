package cn.lmjia.market.core.row.field;

import cn.lmjia.market.core.row.FieldDefinition;
import org.springframework.http.MediaType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 基本字段定义，仅根据表达式(Expression)
 *
 * @param <T> 它可以处理的root的类型
 * @author CJ
 */
public class BasicExpressionField<T> implements FieldDefinition<T> {

    private final Function<Root<T>, Expression<?>> toExpression;
    private final BiFunction<Root<T>, CriteriaBuilder, Expression<?>> toExpression2;
    private final String name;

    BasicExpressionField(String name, Function<Root<T>, Expression<?>> toExpression) {
        this.name = name;
        this.toExpression = toExpression;
        this.toExpression2 = null;
    }

    BasicExpressionField(String name, BiFunction<Root<T>, CriteriaBuilder, Expression<?>> toExpression) {
        this.name = name;
        this.toExpression = null;
        this.toExpression2 = toExpression;
    }

    @Override
    public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root) {
        return innerExpression(criteriaBuilder, root);
    }

    private Expression<?> innerExpression(CriteriaBuilder criteriaBuilder, Root<T> root) {
        if (toExpression2 != null)
            return toExpression2.apply(root, criteriaBuilder);
        if (toExpression != null)
            return toExpression.apply(root);
        throw new IllegalStateException("没有表达式的BasicExpressionField");
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
    public Expression<?> order(Root<T> root, CriteriaBuilder criteriaBuilder) {
        return innerExpression(criteriaBuilder, root);
    }
}
