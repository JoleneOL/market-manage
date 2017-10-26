package cn.lmjia.cash.transfer.cjb.message;

import cn.lmjia.cash.transfer.cjb.message.transfer.XferTrnRq;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 转账与余额查询根标签
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Securities_msgsRQV1 implements Serializable{


    /**
     * 余额查询根标签
     */
    @JsonProperty("SCUSTSTMTTRNRQ")
    private ScustStmttrnRq ScustStmttrnRq;

    /**
     * 转账信息根标签
     */
    @JsonProperty("XFERTRNRQ")
    private XferTrnRq xferTrnRq;
}
