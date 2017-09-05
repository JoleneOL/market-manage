package cn.lmjia.market.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间线单元
 *
 * @author CJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLineUnit {
    /**
     * 事项
     */
    private String topic;
    /**
     * 时间相关
     */
    private Object temporal;

    /**
     * 是否已完成
     */
    private boolean success;
    /**
     * 是否还未启动
     */
    private boolean wait;
}
