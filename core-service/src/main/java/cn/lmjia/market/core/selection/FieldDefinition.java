package cn.lmjia.market.core.selection;

import org.springframework.http.MediaType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.Function;

/**
 * 字段定义
 *
 * @author CJ
 */
public interface FieldDefinition {
    /**
     * @param criteriaBuilder cb
     * @param query           查询
     * @param root            from
     * @return 要作为结果集的目标;null 表示本字段依赖其他字段的查询结果，通常这个组合里只有一个是非null的
     */
    javax.persistence.criteria.Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<?> root);

    /**
     * @return 字段名称
     */
    String name();

    /**
     * 导出结果字段
     *
     * @param origin    原数据
     * @param mediaType 意图导出的结果类型
     * @param exportMe  如果origin为List，并且元素类型等同原数据的上级结果类型并且输出结果为重复的，则可以使用该function
     * @return 输出数据
     */
    Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe);

    /**
     * @param root root
     * @return 排序表达式; null 表示该字段并不支持排序
     */
    Expression<?> order(Root<?> root);
}
