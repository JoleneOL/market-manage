package cn.lmjia.market.core.selection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
public class JQueryDataTableDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root<?> root) {
        // todo sort
        // order[0][column]:2
        // order[0][dir]:desc

        // 首先循环i from 0; 即可获知存在或者指向 column索引 以及方向
        // 若column索引可以确定 FieldDefinition 则按此定义的
//        criteriaBuilder.desc(fields.get(0).order(root));
        return null;
    }

    @Override
    public String getOffsetParameterName() {
        return "start";
    }

    @Override
    public int getDefaultSize() {
        return 10;
    }

    @Override
    public String getSizeParameterName() {
        return "length";
    }

    @Override
    public MediaType toMediaType() {
        return mediaType;
    }


    private int queryDraw(NativeWebRequest webRequest) {
        return readAsInt(webRequest, "draw", 0);
    }


    @Override
    protected void writeResponse(long total, List<Object> rows, NativeWebRequest webRequest) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("draw", queryDraw(webRequest));
        json.put("recordsFiltered", total);
        json.put("recordsTotal", total);
        json.put("data", rows);

        objectMapper.writeValue(webRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
    }

}
