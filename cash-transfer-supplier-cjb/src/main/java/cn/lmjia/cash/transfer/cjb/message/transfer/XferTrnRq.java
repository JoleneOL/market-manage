package cn.lmjia.cash.transfer.cjb.message.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 转账服务请求
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferTrnRq implements Serializable{

    private static final long serialVersionUID = -1785726284902086544L;

    /**
     * 客户端交易的唯一标志，否则客户端将无法分辨响应报文的对应关系,最大30位,建议值为YYYYMMDD+序号
     */
    @JsonProperty("TRNUID")
    private String trnuId;

    /**
     * 在响应报文中包含该内容,非必须输
     */
    @JsonProperty("CLTCOOKIE")
    private String cltCookie;

    @JsonProperty("XFERRQ")
    private XferRq xferRq;

}
