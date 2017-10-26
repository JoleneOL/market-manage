package cn.lmjia.cash.transfer.cjb.message.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 指令处理状态
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferPrcsts implements Serializable{
    private static final long serialVersionUID = 784960292223224910L;

    /**
     * 状态
     */
    @JsonProperty("XFERPRCCODE")
    private String xferPrcCode;

    /**
     * 指令处理时间
     */
    @JsonProperty("DTXFERPRC")
    private String dtXferPrc;

    /**
     * 指令处理信息（非必回）
     */
    @JsonProperty("MESSAGE")
    private String message;
}
