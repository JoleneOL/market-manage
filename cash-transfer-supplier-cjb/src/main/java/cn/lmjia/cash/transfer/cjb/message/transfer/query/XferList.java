package cn.lmjia.cash.transfer.cjb.message.transfer.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferList implements Serializable{
    private static final long serialVersionUID = -953021982503888210L;

    @JacksonXmlProperty(isAttribute = true)
    private String more;

    @JsonProperty("FROM")
    private String from;

    @JsonProperty("TO")
    private String to;

    @JsonProperty("XFER")
    private Xfer xfer;
}
