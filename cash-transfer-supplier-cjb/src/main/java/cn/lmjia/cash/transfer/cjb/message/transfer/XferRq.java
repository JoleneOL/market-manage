package cn.lmjia.cash.transfer.cjb.message.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 生成转账付出指令内容
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferRq implements Serializable {
    private static final long serialVersionUID = 7704214432398893803L;

    /**
     * 转账信息
     */
    @JsonProperty("XFERINFO")
    private XferInfo xferInfo;
}
