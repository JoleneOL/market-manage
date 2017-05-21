package cn.lmjia.market.core.row.field;

import cn.lmjia.market.core.row.FieldDefinition;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author CJ
 */
public class Fields {
    /**
     * @param name
     * @param <T>
     * @return 基础字段
     */
    public static <T> FieldDefinition<T> asBasic(String name) {
        return new BasicField<>(name);
    }

    /**
     * @param name
     * @param function
     * @param <T>
     * @return 特定表达式字段
     */
    public static <T> FieldDefinition<T> asFunction(String name, Function<Root<T>, Expression<?>> function) {
        return new BasicExpressionField<>(name, function);
    }

    /**
     * @param name
     * @param function
     * @param <T>
     * @return 特定表达式字段
     */
    public static <T> FieldDefinition<T> asBiFunction(String name, BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        return new BasicExpressionField<>(name, function);
    }
}
