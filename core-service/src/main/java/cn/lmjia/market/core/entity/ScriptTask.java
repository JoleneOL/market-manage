package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.Instant;

/**
 * 脚本任务
 * 特定时间执行特定脚本；失败则调用失败脚本；成功则调用成功脚本
 * 为了确保事务独立每一个脚本都会做在一个独立的{@link javax.script.ScriptEngine}中执行。
 * 而且会确保其中有可用的变量
 * <ul>
 * <li>context {@link org.springframework.context.ApplicationContext}</li>
 * <li>_this {@link ScriptTask 任务自身}</li>
 * <li>_repository {@link cn.lmjia.market.core.repository.ScriptTaskRepository}</li>
 * </ul>
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ScriptTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 任务名称，可以简单描述下
     */
    @Column(length = 50)
    private String name;
    /**
     * 成功之后则移除该任务
     */
    private boolean removeOnSuccess = true;
    /**
     * 已执行次数
     */
    private int executedCount;
    /**
     * 调用时间
     */
    @Column(columnDefinition = "timestamp")
    private Instant targetInstant;

    @Lob
    private String code;
    @Lob
    private String successCode;
    @Lob
    private String failedCode;

}
