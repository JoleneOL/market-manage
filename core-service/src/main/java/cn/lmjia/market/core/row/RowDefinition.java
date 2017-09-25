package cn.lmjia.market.core.row;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
     * 该方法是除了{@link #entityClass()}外最早运行的方法
     *
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
    default Expression<?> count(CriteriaQuery<Long> countQuery, CriteriaBuilder criteriaBuilder, Root<T> root) {
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

    /**
     * @param cb    cb
     * @param query query
     * @param root  root
     * @return 数据查询时的分组
     */
    default CriteriaQuery<T> dataGroup(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
        return query;
    }

    /**
     * @param cb    cb
     * @param query query
     * @param root  root
     * @return 统计查询时的分组
     */
    default CriteriaQuery<Long> countQuery(CriteriaBuilder cb, CriteriaQuery<Long> query, Root<T> root) {
        return query;
    }
}
