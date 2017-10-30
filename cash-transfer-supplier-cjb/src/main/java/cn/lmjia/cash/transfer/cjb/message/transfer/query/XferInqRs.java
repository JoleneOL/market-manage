package cn.lmjia.cash.transfer.cjb.message.transfer.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferInqRs implements Serializable {

    private static final long serialVersionUID = 3736612447674098510L;
    @JsonProperty("XFERLIST ")
    private XferList xferList;
}
