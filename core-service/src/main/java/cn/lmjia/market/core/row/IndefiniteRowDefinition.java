package cn.lmjia.market.core.row;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * 不支持统计数量的查询定义
 *
 * @author CJ
 */
public interface IndefiniteRowDefinition {
    /**
     * @return 所有字段定义
     */
    List<IndefiniteFieldDefinition> fields();

    /**
     * @param entityManager em
     * @return 建立查询
     */
    Query createQuery(EntityManager entityManager);
}
