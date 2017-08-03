package me.jiangcai.logistics.haier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.jiangcai.logistics.haier.util.BooleanDeserializer;
import me.jiangcai.logistics.haier.util.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutInStore {
    @JsonProperty("orderno")
    private String orderNo;
    @JsonProperty("expno")
    private String expNo;
    @JsonProperty("bustype")
    private String type;
    @JsonProperty("ordertype")
    private String orderType;
    @JsonProperty("outindate")
    @JsonDeserialize(converter = LocalDateTimeConverter.class)
    private LocalDateTime date;
    @JsonProperty("storecode")
    private String storeCode;
    @JsonProperty("iscomplete")
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean complete;
    //      <attributes>
//    <certification>4974752222</certification>
//  </attributes>
    @JsonProperty("InOutItems")
    private List<InOutItem> items;


}
