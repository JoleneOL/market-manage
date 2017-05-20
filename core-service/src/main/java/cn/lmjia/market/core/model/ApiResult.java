package cn.lmjia.market.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author CJ
 */
@Data
public class ApiResult {

    private final Object data;
    @JsonProperty("resultCode")
    private final int code;
    @JsonProperty("resultMsg")
    private final String message;

    public static ApiResult withCodeAndMessage(int code, String message, Object data) {
        return new ApiResult(data, code, message);
    }

    public static ApiResult withCode(int code, Object data) {
        return withCodeAndMessage(code, "ok", data);
    }

    public static ApiResult withOk(Object data) {
        return withCode(200, data);
    }

    public static ApiResult withOk() {
        return withOk(null);
    }

}
