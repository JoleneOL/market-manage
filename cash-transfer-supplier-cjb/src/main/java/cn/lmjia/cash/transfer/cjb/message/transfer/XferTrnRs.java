package cn.lmjia.cash.transfer.cjb.message.transfer;

import cn.lmjia.cash.transfer.cjb.message.Status;
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
public class XferTrnRs implements Serializable {
    private static final long serialVersionUID = 7123732046460700619L;

    @JsonProperty("TRNUID")
    private String trnuId;


    @JsonProperty("STATUS")
    private Status status;

    @JsonProperty("XFERRS")
    private XferRs xferRs;

    @JsonProperty("CLTCOOKIE")
    private String cltCookie;

}
