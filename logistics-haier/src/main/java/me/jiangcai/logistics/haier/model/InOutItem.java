package me.jiangcai.logistics.haier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.jiangcai.logistics.haier.util.BooleanDeserializer;

import java.io.Serializable;

/**
 * @author CJ
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InOutItem implements Serializable {

    private static final long serialVersionUID = 5928930928174888678L;

    @JsonProperty("storagetype")
    private String type;
    @JsonProperty("productcode")
    private String productCode;
    @JsonProperty("hrcode")
    private String hrCode;
    @JsonProperty("prodes")
    private String productModel;
    private int number;
    @JsonProperty("iscomplete")
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean complete;
}
