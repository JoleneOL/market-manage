package cn.lmjia.cash.transfer.cjb.message;

import cn.lmjia.cash.transfer.cjb.message.transfer.XferTrnRs;
import cn.lmjia.cash.transfer.cjb.message.transfer.query.XferInqTrnRs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 余额查询响应信息根标签
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Securities_msgsRSV1 {


    /**
     * 0-n， 对应请求的响应，可能包括同步的内容
     */
    @JsonProperty("SCUSTSTMTRS")
    private ScustStmtrs scustStmtrs;

    /**
     * 转账响应内容
     */
    @JsonProperty("XFERTRNRS")
    private XferTrnRs xferTrnRs;

    /**
     * 查询转账状态响应.
     */
    @JsonProperty("XFERINQTRNRS")
    private XferInqTrnRs xferInqTrnRs;
}
