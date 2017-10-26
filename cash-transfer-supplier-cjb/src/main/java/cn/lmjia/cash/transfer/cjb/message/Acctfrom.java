package cn.lmjia.cash.transfer.cjb.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ACCTFROM,付款人账号
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Acctfrom implements Serializable{

    private static final long serialVersionUID = 3340258829840996987L;

    /**
     * 付款账号  18位
     */
    @JsonProperty("ACCTID")
    private String acctId;

    /**
     * 付款人姓名,选填 最大50位
     */
    @JsonProperty("NAME")
    private String name;

    /**
     * 开户行,选填 仅在报文中体现
     */
    @JsonProperty("BANKDESC")
    private String bankDesc;

    /**
     * 城市 ,选填 仅在报文中体现
     */
    @JsonProperty("CITY")
    private String city;
}
