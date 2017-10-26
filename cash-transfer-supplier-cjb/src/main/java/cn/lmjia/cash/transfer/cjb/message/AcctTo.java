package cn.lmjia.cash.transfer.cjb.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 收款人账户信息
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcctTo implements Serializable {
    private static final long serialVersionUID = -2617876873057298233L;

    /**
     * 是否行内账户，INTERBANK=”Y/N” ；
     */
    @JacksonXmlProperty(isAttribute = true,localName = "INTERBANK")
    private String interBank;

    /**
     * 是否同城转账， LOCAL=”Y/N”。
     */
    @JacksonXmlProperty(isAttribute = true,localName = "LOCAL")
    private String local;

    /**
     * 收款账号只允许是数字、英文字母和“－”，如果不符合以上格式，系统返回“收款账号字段格式错误”，最大32位
     */
    @JsonProperty("ACCTID")
    private String acctId;

    /**
     * 收款人名称，最大50位
     */
    @JsonProperty("NAME")
    private String name;

    /**
     * 收款人开户行名称。非兴业银行账号时，收款人开户行名称必输,且不应包含“兴业银行”字样，否则返回“您的收款账号不是兴业银行账号”并不允许提交，最大50位
     */
    @JsonProperty("BANKDESC")
    private String bankDesc;

    /**
     * 收款人收报行号，12位
     */
    @JsonProperty("BANKNUM")
    private String bankNum;

    /**
     * 收款人城市，同城无需填写，异地汇款填入收款行城市名称（建议客户端控制，否则可能不能正常转账），最大30位
     */
    @JsonProperty("CITY")
    private String city;

    /**
     * 是否转向财务公司内部账户，非必输1-是，0或空-否
     */
    @JsonProperty("COLLECT")
    private String collect;
}
