package cn.lmjia.cash.transfer.cjb.message.sonrq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录信息根标签
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignonMsgsRQV1 implements Serializable {
    private static final long serialVersionUID = -6388773982512621310L;
    @JsonProperty("SONRQ")
    private Sonrq sonrq;

}
