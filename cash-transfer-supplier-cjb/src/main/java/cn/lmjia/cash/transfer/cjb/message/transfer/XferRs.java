package cn.lmjia.cash.transfer.cjb.message.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferRs implements Serializable{

    private static final long serialVersionUID = 4216179870134996043L;

    @JsonProperty("SRVRID")
    private String srvrId;

    @JsonProperty("XFERINFO")
    private XferInfo xferInfo;

    @JsonProperty("XFERPRCSTS")
    private XferPrcsts xferPrcsts;

    /**
     *客户参考, 客户端转账流水.
     */
    @JsonProperty("CLIENTREF")
    private String clientref;
}
