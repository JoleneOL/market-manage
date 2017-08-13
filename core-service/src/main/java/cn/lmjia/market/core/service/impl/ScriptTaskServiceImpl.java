package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ScriptTask;
import cn.lmjia.market.core.repository.ScriptTaskRepository;
import cn.lmjia.market.core.service.ScriptTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author CJ
 */
@Service
public class ScriptTaskServiceImpl implements ScriptTaskService {

    private static final Log log = LogFactory.getLog(ScriptTaskServiceImpl.class);
    @Autowired
    private ScriptTaskRepository scriptTaskRepository;

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

}
