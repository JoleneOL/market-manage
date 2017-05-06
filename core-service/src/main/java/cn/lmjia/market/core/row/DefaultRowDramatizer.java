package cn.lmjia.market.core.row;

import org.springframework.http.MediaType;
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
public class DefaultRowDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {
    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root<?> root) {
        return Collections.emptyList();
    }


    public String getOffsetParameterName() {
        return "offset";
    }

    public int getDefaultSize() {
        return 10;
    }

    public String getSizeParameterName() {
        return "size";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeResponse(long total, List<Object> rows, NativeWebRequest webRequest) throws IOException {
// i do not know
    }

}
