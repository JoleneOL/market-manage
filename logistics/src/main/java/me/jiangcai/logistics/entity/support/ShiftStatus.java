package me.jiangcai.logistics.entity.support;

/**
 * @author CJ
 */
public enum ShiftStatus {
    /**
     * 初始状态，应该被扣除可用量
     */
    init,
    /**
     * 被接受，进入处理状态，应该被扣除可用量
     */
    accept,
    /**
     * 被拒绝，无需扣除可用量
     */
    reject,
    /**
     * 移动状态，应该被扣除可用量
     */
    movement,
    /**
     * 最终没有成功，应该需要后续ShiftUnit继续支持，应该被扣除可用量
     * 可结算
     */
    failed,
    /**
     * 最终成功，应该被扣除可用量
     * 可结算
     */
    success
}
