package cn.lmjia.market.core.row;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 数据定义
 *
 * @param <T> 数据来自的JPA Entity范型
 * @author CJ
 */
public interface RowDefinition<T> {

    /**
     * @return 最初查询的实体也就是使用哪个 {@link javax.persistence.criteria.Root}
     */
    Class<T> entityClass();

    /**
     * @return 所有字段定义
     */
    List<FieldDefinition<T>> fields();

    /**
     * @return 数据规格;可以为null
     */
    Specification<T> specification();

    /**
     * @return 以何表达式作为count参数
     */
    default Expression<?> count(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return root;
    }

    /**
     * @param criteriaBuilder cb
     * @param root            root
     * @return 默认排序，如果请求没有明示排序需求；null表示无默认排序
     */
    default List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return null;
    }
}
