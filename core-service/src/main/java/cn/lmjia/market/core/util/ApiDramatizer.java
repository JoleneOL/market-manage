package cn.lmjia.market.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.crud.row.AbstractMediaRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import org.springframework.data.domain.Page;
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
public class ApiDramatizer extends AbstractMediaRowDramatizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root root) {
        return null;
    }

    @Override
    public String getOffsetParameterName() {
        return null;
    }

    @Override
    public int getDefaultSize() {
        return 10;
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        return readAsInt(webRequest, "page", 0) * querySize(webRequest);
    }

    @Override
    public String getSizeParameterName() {
        return "size";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> list, NativeWebRequest nativeWebRequest) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("resultCode", 200);
        json.put("resultMsg", "ok");
        json.put("total_count", page.getTotalElements());
        json.put("data", list);
        // 实际上……
//  "incomplete_results": false,
//        json.put("incomplete_results", total < queryOffset(webRequest) + querySize(webRequest));

        objectMapper.writeValue(nativeWebRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
    }

//    @Override
//    protected void writeResponse(long total, List<Object> rows, NativeWebRequest webRequest) throws IOException {
//        Map<String, Object> json = new HashMap<>();
//        json.put("resultCode", 200);
//        json.put("resultMsg", "ok");
//        json.put("total_count", total);
//        json.put("data", rows);
//        // 实际上……
////  "incomplete_results": false,
////        json.put("incomplete_results", total < queryOffset(webRequest) + querySize(webRequest));
//
//        objectMapper.writeValue(webRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
//    }
}
