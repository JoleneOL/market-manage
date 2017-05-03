package cn.lmjia.market.core.data_table;

import cn.lmjia.market.core.selection.FieldDefinition;
import cn.lmjia.market.core.selection.RowDramatizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.util.NumberUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author CJ
 */
public class JQueryDataTableDramatizer implements RowDramatizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder, Root<?> root) {
        return null;
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter("start"), Integer.class);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public int querySize(NativeWebRequest webRequest) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter("length"), Integer.class);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private int queryDraw(NativeWebRequest webRequest) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter("draw"), Integer.class);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public void writeResponse(long total, List<?> list, List<FieldDefinition> fields, NativeWebRequest webRequest)
            throws IOException {
        final HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);

        nativeResponse.setHeader("Content-Type", "application/json;charset=UTF-8");

        List<Object> rows = drawToRows(list, fields);

        Map<String, Object> json = new HashMap<>();
        json.put("draw", queryDraw(webRequest));
        json.put("recordsFiltered", total);
        json.put("recordsTotal", total);
        json.put("data", rows);

        objectMapper.writeValue(nativeResponse.getOutputStream(), json);
    }


    private List<Object> drawToRows(List<?> list, List<FieldDefinition> fields) {
        List<Object> rows = new ArrayList<>();
        Function<List, ?> function = (input) -> drawToRows(input, fields);
        for (Object data : list) {
// data 通常为一个Object[] 然后fields逐个描述它
            HashMap<String, Object> outData = new HashMap<>();
            if (data.getClass().isArray()) {
                assert Array.getLength(data) == fields.size();
                for (int i = 0; i < fields.size(); i++) {
                    FieldDefinition fieldDefinition = fields.get(i);
                    outData.put(fieldDefinition.name(), fieldDefinition.export(Array.get(data, i), mediaType, function));
                }
            } else {
                // 只有一个结果？
                for (FieldDefinition fieldDefinition : fields) {
                    outData.put(fieldDefinition.name(), fieldDefinition.export(data, mediaType, function));
                }
            }

            rows.add(outData);
        }
        return rows;
    }
}
