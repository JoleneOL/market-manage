package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.ScriptTask;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * 脚本任务服务
 *
 * @author CJ
 * @see cn.lmjia.market.core.entity.ScriptTask
 */
public interface ScriptTaskService {

    /**
     * 提交一个完成后就自动销毁的任务
     *
     * @param name            名字
     * @param removeOnSuccess 是否在成功调用后就删除
     * @param instant         调用时刻
     * @param code            脚本
     * @param successCode     成功脚本
     * @param failedCode      失败脚本
     * @return 新增任务
     */
    @Transactional
    ScriptTask submitTask(String name, boolean removeOnSuccess, Instant instant, String code, String successCode, String failedCode);

    /**
     * 提交一个完成后就自动销毁的任务
     *
     * @param name        名字
     * @param instant     调用时刻
     * @param code        脚本
     * @param successCode 成功脚本
     * @param failedCode  失败脚本
     * @return 新增任务
     */
    @Transactional
    ScriptTask submitTask(String name, Instant instant, String code, String successCode, String failedCode);

    /**
     * 提交一个完成后就自动销毁的任务
     *
     * @param name        名字
     * @param instant     调用时刻
     * @param code        脚本
     * @param successCode 成功脚本
     * @return 新增任务
     */
    @Transactional
    ScriptTask submitTask(String name, Instant instant, String code, String successCode);

    /**
     * 提交一个完成后就自动销毁的任务
     *
     * @param name    名字
     * @param instant 调用时刻
     * @param code    脚本
     * @return 新增任务
     */
    @Transactional
    ScriptTask submitTask(String name, Instant instant, String code);

    @Scheduled(fixedDelay = 500)
    @Transactional
    void go();
}
