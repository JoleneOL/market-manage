package me.jiangcai.logistics.event;

import lombok.Data;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.StockShiftUnit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发货后事件
 * @author lxf
 */
@Data
public class DeliveryGoodsSuccessEvent {
    /**
     * 快递信息.
     */
    private final StockShiftUnit unit;
    /**
     * 收件人手机号,也是要接收短信的人.
     */
    private final String consigneeMobile;
}
