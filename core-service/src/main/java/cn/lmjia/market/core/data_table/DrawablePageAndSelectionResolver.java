package cn.lmjia.market.core.data_table;

import cn.lmjia.market.core.selection.Selection;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
public class DrawablePageAndSelectionResolver implements HandlerMethodReturnValueHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == DrawablePageAndSelection.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        DrawablePageAndSelection pageAndSelection = (DrawablePageAndSelection<?>) returnValue;

        Map<String, Object> json = new HashMap<>();
        json.put("draw", pageAndSelection.getDataPageable().getDraw());
        json.put("recordsFiltered", pageAndSelection.getPage().getTotalElements());
        json.put("recordsTotal", pageAndSelection.getPage().getTotalElements());
        List<Object> rows = new ArrayList<>();
        drawToRows(pageAndSelection.getPage(), pageAndSelection.getSelectionList(), rows);
        json.put("data", rows);

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        objectMapper.writeValue(response.getOutputStream(), json);
        mavContainer.setRequestHandled(true);
    }

    private void drawToRows(Iterable list, List<Selection> selections, List<Object> rows) {
        for (Object data : list) {
            Map<String, Object> row = new HashMap<>();
//            Selections selections = null;
            for (Object selectionObject : selections) {
                Selection selection = (Selection) selectionObject;
                if (selection.supportIterable()) {
                    List<Object> newList = new ArrayList<>();
                    drawToRows((Iterable) selection.selectData(data), selections, newList);
                    row.put(selection.getName(), newList);
                } else
                    row.put(selection.getName(), selection.selectData(data));
            }
//            for (Object selectionObject : pageAndSelection.getSelectionList()) {
//                Selection selection = (Selection) selectionObject;
//                row.put(selection.getName(), selection.selectData(data));
//            }
            rows.add(row);
        }
    }
}
