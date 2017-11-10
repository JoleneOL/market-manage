package cn.lmjia.market.core.row;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author CJ
 */
public interface RowService {

    /**
     * @param definition 数据定义
     * @param <T>        实体类型
     * @return 根据查询定义，获取所有的实体
     */
    @Transactional(readOnly = true)
    <T> List<T> queryAllEntity(RowDefinition<T> definition);

}
