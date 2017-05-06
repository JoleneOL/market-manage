package cn.lmjia.market.core.row;

import org.springframework.data.jpa.domain.Specification;

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
    List<FieldDefinition> fields();

    /**
     * @return 数据规格;可以为null
     */
    Specification<T> specification();

}
