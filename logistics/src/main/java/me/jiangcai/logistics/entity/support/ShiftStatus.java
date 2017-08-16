package me.jiangcai.logistics.entity.support;

/**
 * @author CJ
 */
public enum ShiftStatus {
    /**
     * 初始状态，应该被扣除可用量
     */
    init("初始化"),
    /**
     * 被接受，进入处理状态，应该被扣除可用量
     */
    accept("已接单"),
    /**
     * 被拒绝，无需扣除可用量；核心意义是没开始就结束了
     * 并非被拒收！而是这个移动没有获得许可！
     */
    reject("已拒绝"),
    /**
     * 移动状态，应该被扣除可用量
     */
    movement("运递中"),
    /**
     * 核心意义是开始运送了，但因为各种原因失败了
     * 最终没有成功，应该需要后续ShiftUnit继续支持，应该被扣除可用量
     * 可结算
     */
    failed("运递失败"),
    /**
     * 最终成功，应该被扣除可用量
     * 可结算
     */
    success("已送达");
    private final String message;

    ShiftStatus(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
