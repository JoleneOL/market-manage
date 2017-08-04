package me.jiangcai.logistics.haier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.jiangcai.logistics.haier.util.BooleanDeserializer;

/**
 * @author CJ
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InOutItem {
    @JsonProperty("storagetype")
    private String type;
    @JsonProperty("productcode")
    private String productCode;
    @JsonProperty("hrcode")
    private String hrCode;
    @JsonProperty("prodes")
    private String name;
    private int number;
    @JsonProperty("iscomplete")
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean complete;
}
