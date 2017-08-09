package me.jiangcai.logistics.event;

import lombok.Data;
import me.jiangcai.logistics.entity.StockShiftUnit;

import java.time.LocalDateTime;

/**
 * 安装完成事件
 * 应当确保在事务内发起该事件！不然必然出问题！
 * 如果物流供应商没有提供安装信息，可以提供null
 *
 * @author CJ
 */
@Data
public class InstallationEvent {
    /**
     * 事务内实体
     */
    private final StockShiftUnit unit;
    private final String installer;
    private final String installCompany;
    private final String mobile;
    /**
     * 不可为null
     */
    private final LocalDateTime installTime;
}
