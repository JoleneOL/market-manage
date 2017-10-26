package cn.lmjia.cash.transfer.cjb.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 处理结果状态
 * @author lxf
 */
@Data
public class Status implements Serializable {
    private static final long serialVersionUID = -3603396219014252055L;

    /**
     * 处理结果码
     */
    @JsonProperty("CODE")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    /**
     * 处理结果等级
     */
    @JsonProperty("SEVERITY")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String severity;

    /**
     * 信息描述
     */
    @JsonProperty("MESSAGE")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
