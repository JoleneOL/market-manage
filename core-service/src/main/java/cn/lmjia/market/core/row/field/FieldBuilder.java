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
 * 字段构建器
 *
 * @author CJ
 */
public class FieldBuilder<T> {

    private final String name;
    private BiFunction<Root<T>, CriteriaBuilder, Expression<?>> biSelect;
    private Function<Root<T>, Expression<?>> select;
    private BiFunction<Object, MediaType, Object> format;
    private Function<Root<T>, Expression<?>> order;
    private BiFunction<Root<T>, CriteriaBuilder, Expression<?>> biOrder;
    private boolean noOrder = false;

    private FieldBuilder(String name) {
        this.name = name;
    }

    /**
     * 开始一个构造器
     *
     * @param name {@link FieldDefinition#name()}
     * @param <Y>  动态类型
     * @return 新的构造器
     */
    public static <Y> FieldBuilder<Y> asName(Class<Y> type, String name) {
        return new FieldBuilder<>(name);
    }

    /**
     * 开始一个构造器
     *
     * @param name {@link FieldDefinition#name()}
     * @return 新的构造器
     */
    public static <X> FieldBuilder<X> asName(String name) {
        return new FieldBuilder<X>(name);
    }

    public FieldBuilder<T> addBiSelect(BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        this.biSelect = function;
        return this;
    }

    public FieldBuilder<T> addSelect(Function<Root<T>, Expression<?>> function) {
        this.select = function;
        return this;
    }

    public FieldBuilder<T> addBiOrder(BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        this.biOrder = function;
        return this;
    }

    public FieldBuilder<T> addOrder(Function<Root<T>, Expression<?>> function) {
        this.order = function;
        return this;
    }

    public FieldBuilder<T> withOrder() {
        this.noOrder = true;
        return this;
    }

    public FieldBuilder<T> addFormat(BiFunction<Object, MediaType, Object> format) {
        this.format = format;
        return this;
    }

    public FieldDefinition<T> build() {
        return new FieldDefinition<T>() {
            @Override
            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root) {
                if (biSelect != null)
                    return biSelect.apply(root, criteriaBuilder);
                if (select != null)
                    return select.apply(root);
                return root.get(name);
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                if (format != null)
                    return format.apply(origin, mediaType);
                return origin;
            }

            @Override
            public Expression<?> order(Root<T> root, CriteriaBuilder criteriaBuilder) {
                if (noOrder)
                    return null;
                if (biOrder != null)
                    return biOrder.apply(root, criteriaBuilder);
                if (order != null)
                    return order.apply(root);
                if (biSelect != null)
                    return biSelect.apply(root, criteriaBuilder);
                if (select != null)
                    return select.apply(root);
                return root.get(name);
            }
        };
    }


}
