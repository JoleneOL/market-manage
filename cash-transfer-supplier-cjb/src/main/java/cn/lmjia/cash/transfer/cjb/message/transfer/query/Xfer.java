package cn.lmjia.cash.transfer.cjb.message.transfer.query;

import cn.lmjia.cash.transfer.cjb.message.transfer.XferInfo;
import cn.lmjia.cash.transfer.cjb.message.transfer.XferPrcsts;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Xfer implements Serializable {

    private static final long serialVersionUID = -2755476215470011322L;

    /**
     * 服务器交易ID
     */
    @JsonProperty("SRVRTID")
    private String srvrtId;

    /**
     * 转账内容信息
     */
    @JsonProperty("XFERINFO")
    private XferInfo xferInfo;

    /**
     * 指令处理状态
     */
    @JsonProperty("XFERPRCSTS")
    private XferPrcsts xferPrcsts;
}
