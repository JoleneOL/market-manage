package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ScriptTask;
import cn.lmjia.market.core.repository.ScriptTaskRepository;
import cn.lmjia.market.core.service.ScriptTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.time.Instant;

/**
 * @author CJ
 */
@Service
public class ScriptTaskServiceImpl implements ScriptTaskService {

    private static final Log log = LogFactory.getLog(ScriptTaskServiceImpl.class);
    @Autowired
    private ScriptTaskRepository scriptTaskRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ScriptTask submitTask(String name, boolean removeOnSuccess, Instant instant, String code, String successCode
            , String failedCode) {
        log.debug("planning to execute " + code);
        ScriptTask task = new ScriptTask();
        task.setName(name);
        task.setRemoveOnSuccess(removeOnSuccess);
        task.setTargetInstant(instant);
        task.setCode(code);
        task.setSuccessCode(successCode);
        task.setFailedCode(failedCode);
        return scriptTaskRepository.save(task);
    }

    @Override
    public ScriptTask submitTask(String name, Instant instant, String code, String successCode, String failedCode) {
        return submitTask(name, true, instant, code, successCode, failedCode);
    }

    @Override
    public ScriptTask submitTask(String name, Instant instant, String code, String successCode) {
        return submitTask(name, instant, code, successCode, null);
    }

    @Override
    public ScriptTask submitTask(String name, Instant instant, String code) {
        return submitTask(name, instant, code, null);
    }

    @Scheduled(fixedDelay = 500)
    @Override
    public void go() {
        Instant now = Instant.now();
        // 过滤掉 执行次数超过100次的
        for (ScriptTask task : scriptTaskRepository.findAll((root, query, cb)
                -> cb.and(cb.lessThanOrEqualTo(root.get("targetInstant"), now)
                , cb.lessThan(root.get("executedCount"), 100)))) {
            log.debug("going execute " + task.getName());
            task.setExecutedCount(task.getExecutedCount() + 1);
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("js");
            scriptEngine.put("context", applicationContext);
            scriptEngine.put("_this", task);
            scriptEngine.put("_repository", scriptTaskRepository);
            try {
                scriptEngine.eval(task.getCode());
                if (!StringUtils.isEmpty(task.getSuccessCode())) {
                    try {
                        scriptEngine.eval(task.getSuccessCode());
                    } catch (Throwable all) {
                        log.info("ScriptTask Success", all);
                    }
                }
                if (task.isRemoveOnSuccess())
                    scriptTaskRepository.delete(task);
            } catch (Exception ex) {
                //调用失败
                log.trace("ScriptTask Exception", ex);
                if (!StringUtils.isEmpty(task.getFailedCode())) {
                    try {
                        scriptEngine.eval(task.getFailedCode());
                    } catch (Throwable all) {
                        log.info("ScriptTask Failed", all);
                    }
                }
            }
        }
    }
}
