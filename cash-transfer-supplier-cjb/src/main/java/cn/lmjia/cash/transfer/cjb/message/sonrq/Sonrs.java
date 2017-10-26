package cn.lmjia.cash.transfer.cjb.message.sonrq;

import cn.lmjia.cash.transfer.cjb.message.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sonrs implements Serializable {
    private static final long serialVersionUID = -5097674219147501847L;

    /**
     * 交易处理状态 必回
     */
    @JsonProperty("STATUS")
    private Status status;

    /**
     * 服务端日期时间，YYYY-MM-DD HH:MM:SS 必回
     */
    @JsonProperty("DTSERVER")
    private String dtServer;

    /**
     * UserKey的有效时间 服务器时间 ,非必回，仅在GENUSERKEY为”Y”时必回
     */
    @JsonProperty("TSKEYEXPIRE")
    private String tsKeyExpire;

    /**
     * 服务器需要保存会话COOKIE，则发送，否则不发送，客户端在下次请求中应包含 ,非必回
     */
    @JsonProperty("SESSCOOKIE")
    private String sessCookie;




}
