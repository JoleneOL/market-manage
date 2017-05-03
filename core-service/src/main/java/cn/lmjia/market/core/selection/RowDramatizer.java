package cn.lmjia.market.core.selection;

import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.List;

/**
 * 数据重新定制者
 *
 * @author CJ
 */
public interface RowDramatizer {

    /**
     * @param fields          要显示的字段
     * @param webRequest      请求
     * @param criteriaBuilder cb
     * @param root            root
     * @return 排序规则
     */
    List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root<?> root);

    /**
     * @param webRequest 请求
     * @return 开始查询位；默认0
     */
    int queryOffset(NativeWebRequest webRequest);

    /**
     * @param webRequest 请求
     * @return 查询长度
     */
    int querySize(NativeWebRequest webRequest);

    /**
     * 写入响应
     *
     * @param total      总数
     * @param list       结果集
     * @param fields     字段
     * @param webRequest 请求
     * @throws IOException 写入时出现的
     */
    void writeResponse(long total, List<?> list, List<FieldDefinition> fields, NativeWebRequest webRequest) throws IOException;
}
