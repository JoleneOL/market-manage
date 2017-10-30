package cn.lmjia.cash.transfer.cjb.message.transfer.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 付款查询请求交易根标签
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferInqTrnRq implements Serializable {
    private static final long serialVersionUID = 2877421553295235466L;

    @JsonProperty("TRNUID")
    private String trnuId;

    @JsonProperty("XFERINQRQ")
    private XferInqRq xferInqRq;
}
