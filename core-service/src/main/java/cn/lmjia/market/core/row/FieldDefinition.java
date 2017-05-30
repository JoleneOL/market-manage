package cn.lmjia.market.core.row;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * 字段定义
 *
 * @param <T> 它可以处理的root的类型
 * @author CJ
 */
public interface FieldDefinition<T> extends IndefiniteFieldDefinition {
    /**
     * @param criteriaBuilder cb
     * @param query           查询
     * @param root            from
     * @return 要作为结果集的目标;null 表示本字段依赖其他字段的查询结果，通常这个组合里只有一个是非null的
     */
    javax.persistence.criteria.Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root);


    /**
     * @param root            root
     * @param criteriaBuilder cb
     * @return 排序表达式; null 表示该字段并不支持排序
     */
    Expression<?> order(Root<T> root, CriteriaBuilder criteriaBuilder);
}
