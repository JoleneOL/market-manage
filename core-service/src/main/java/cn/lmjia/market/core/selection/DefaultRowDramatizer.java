package cn.lmjia.market.core.selection;

import org.springframework.util.NumberUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
public class DefaultRowDramatizer implements RowDramatizer {
    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root<?> root) {
        return Collections.emptyList();
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter("offset"), Integer.class);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public int querySize(NativeWebRequest webRequest) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter("size"), Integer.class);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public void writeResponse(long total, List<?> list, List<FieldDefinition> fields
            , NativeWebRequest webRequest) throws IOException {
        // i do not know
    }
}
