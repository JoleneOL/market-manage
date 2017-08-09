package me.jiangcai.logistics.haier.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.jiangcai.logistics.haier.util.BooleanDeserializer;
import me.jiangcai.logistics.haier.util.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RejectInfo extends AbstractModel {
    private static final long serialVersionUID = 4967397296957184122L;
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
    @JsonProperty("RejectItems")
    private List<InOutItem> items;
    private String content;

}
