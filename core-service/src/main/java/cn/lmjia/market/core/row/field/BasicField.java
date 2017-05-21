package cn.lmjia.market.core.row.field;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.Function;

/**
 * 最基本的字段；一般都是来自一个实体的简单字段；可以排序
 *
 * @author CJ
 */
public class BasicField<T> extends BasicExpressionField<T> {

    BasicField(String name) {
        super(name, new Function<Root<T>, Expression<?>>() {
            @Override
            public Expression<?> apply(Root<T> root) {
                return root.get(name);
            }
        });
    }

}
