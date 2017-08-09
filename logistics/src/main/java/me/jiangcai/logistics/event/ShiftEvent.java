package me.jiangcai.logistics.event;

import lombok.Data;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;

import java.time.LocalDateTime;

/**
 * 物流订单变化事件，应当确保在事务内发起该事件！
 *
 * @author CJ
 */
@Data
public class ShiftEvent {
    /**
     * 事务内实体
     */
    private final StockShiftUnit unit;
    /**
     * 进入的状态
     */
    private final ShiftStatus status;
    private final LocalDateTime time;
    /**
     * 人类可识别的信息
     */
    private final String message;
}
