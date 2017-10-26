package cn.lmjia.cash.transfer.cjb.message.sonrq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求响应
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignonMsgsRSV1 implements Serializable {

    private static final long serialVersionUID = 2836184291398758760L;
    /**
     *  登录响应信息
     */
    @JsonProperty("SONRS")
    private Sonrs sonrs;
}
