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

    List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root<?> root);

    int queryOffset(NativeWebRequest webRequest);

    int querySize(NativeWebRequest webRequest);

    void writeResponse(long total, List<?> list, List<FieldDefinition> fields, NativeWebRequest webRequest) throws IOException;
}
