package cn.lmjia.market.core.row.supplier;

import cn.lmjia.market.core.row.AbstractMediaRowDramatizer;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDramatizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author CJ
 */
public class JQueryDataTableDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    private static final Log log = LogFactory.getLog(JQueryDataTableDramatizer.class);

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder, Root root) {
        // http://localhost:55555/market-manage/dealer/dealer-view/mock/userData.json?draw=4&columns%5B0%5D%5Bdata%5D=name&columns%5B0%5D%5Bname%5D=name&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=rank&columns%5B1%5D%5Bname%5D=rank&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=recommend&columns%5B2%5D%5Bname%5D=recommend&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=joinTime&columns%5B3%5D%5Bname%5D=joinTime&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=province&columns%5B4%5D%5Bname%5D=province&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=city&columns%5B5%5D%5Bname%5D=city&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=district&columns%5B6%5D%5Bname%5D=district&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=address&columns%5B7%5D%5Bname%5D=address&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=phone&columns%5B8%5D%5Bname%5D=phone&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=1&order%5B0%5D%5Bdir%5D=asc&start=0&length=15&search%5Bvalue%5D=&search%5Bregex%5D=false&_=1498641034202
        // order[0][column]:2
        // order[0][dir]:desc
        ArrayList<Order> orderArrayList = new ArrayList<>();
        // 从0开始 很自然
        int i = -1;
        while (true) {
            i++;
            String orderDirString = webRequest.getParameter("order[" + i + "][dir]");
            if (StringUtils.isEmpty(orderDirString))
                return orderArrayList;
            // columns[0][name]:name
            String index = webRequest.getParameter("order[" + i + "][column]");
            String orderName = webRequest.getParameter("columns[" + index + "][name]");

            if (StringUtils.isEmpty(orderName))
                return orderArrayList;
            // 然后我们得确保这个值是支持order的
            @SuppressWarnings("unchecked")
            Expression<?> toOrder = fields.stream()
                    .filter(fieldDefinition -> fieldDefinition.name().equals(orderName))
                    .map(fieldDefinition -> fieldDefinition.order(root, criteriaBuilder))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (toOrder == null) {
                log.debug("try order with no-order-able field:" + orderName);
                continue;
            }

            if ("desc".equalsIgnoreCase(orderDirString)) {
                orderArrayList.add(criteriaBuilder.desc(toOrder));
            } else
                orderArrayList.add(criteriaBuilder.asc(toOrder));
        }

        // 首先循环i from 0; 即可获知存在或者指向 column索引 以及方向
        // 若column索引可以确定 FieldDefinition 则按此定义的
//        criteriaBuilder.desc(fields.get(0).order(root));
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
