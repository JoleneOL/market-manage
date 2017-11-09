package cn.lmjia.cash.transfer.cjb.message;

import cn.lmjia.cash.transfer.cjb.message.sonrq.SignonMsgsRQV1;
import cn.lmjia.cash.transfer.cjb.message.sonrq.SignonMsgsRSV1;
import cn.lmjia.cash.transfer.cjb.message.transfer.XferTrnRq;
import cn.lmjia.cash.transfer.cjb.message.transfer.XferTrnRs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.io.Serializable;

/**
 * 兴业银行报文根对象
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName="FOX")
public class Fox implements Serializable {
    private static final long serialVersionUID = 6873992917373971264L;

    /**
     * 登录请求信息根标签
     */
    @JsonProperty("SIGNONMSGSRQV1")
    private SignonMsgsRQV1 signonMsgsRQV1;

    /**
     * 登录响应信息根标签
     */
    @JsonProperty("SIGNONMSGSRSV1")
    private SignonMsgsRSV1 signonMsgsRSV1;

    /**
     * 余额查询请求根标签
     */
    @JsonProperty("SECURITIES_MSGSRQV1")
    private Securities_msgsRQV1 securities_msgsRQV1;


    /**
     * 余额查询响应根标签
     */
    @JsonProperty("SECURITIES_MSGSRSV1")
    private Securities_msgsRSV1 securities_msgsRSV1;



}
