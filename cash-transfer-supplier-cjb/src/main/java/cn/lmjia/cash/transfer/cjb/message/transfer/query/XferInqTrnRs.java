package cn.lmjia.cash.transfer.cjb.message.transfer.query;

import cn.lmjia.cash.transfer.cjb.message.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferInqTrnRs implements Serializable{
    private static final long serialVersionUID = 6794659983732066927L;

    @JsonProperty("TRUNID")
    private String trnuId;

    @JsonProperty("STATUS")
    private Status status;

    @JsonProperty("XFERINQRS")
    private XferInqRs xferInqRs;
}
