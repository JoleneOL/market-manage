package me.jiangcai.logistics.haier.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.jiangcai.logistics.haier.util.LocalDateTimeConverter;

import java.time.LocalDateTime;

/**
 * 订单同步
 *
 * @author CJ
 */
@Data
public class OrderStatusSync {
    @JsonProperty("orderno")
    private String orderNo;
    @JsonProperty("storecode")
    private String storeCode;
    @JsonProperty("expno")
    private String expNo;
    // 操作员 还是蛮有意思的
    private String operator;
    @JsonProperty("operdate")
    @JsonDeserialize(converter = LocalDateTimeConverter.class)
    private LocalDateTime operateDate;
    /**
     * WMS_ACCEPT -接单
     * WMS_FAILED -拒单
     * TMS_ACCEPT 揽收
     * TMS_REJECT -揽收失败
     * TMS_DELIVERING -派送
     * TMS_STATION_IN -分站进
     * TMS_STATION_OUT -分站出
     * TMS_CHANGE -用户改约
     * TMS_ERROR -异常
     * TMS_SIGN -签收成功
     * TMS_FAILED -拒签
     * TMS_DELIVERY-网点交付
     * TMS_FAILED_IN -转运入库
     * TMS_FAILED_OUT -转运出库
     * TMS_RETURN-拒收入库
     * TMS_RESULT_S-取件成功
     * TMS_RESULT_F-取件失败
     * COD_SUCCESS 扣款成功
     * TMS_DB_CREATE 调拨单生成
     * TMS_DB_OUT调拨单出库
     * TMS_DB_IN 调拨单入库
     */
    private String status;
    /**
     * 状态说明
     */
    private String content;
    private String remark;
    /**
     * 可选
     */
    private String attributes;
}
