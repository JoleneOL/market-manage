package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.repository.ScriptTaskRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ScriptTaskServiceTest extends CoreServiceTest {

    @SuppressWarnings("WeakerAccess")
    public static final java.util.concurrent.atomic.AtomicInteger testValue = new AtomicInteger();
    private static final Log log = LogFactory.getLog(ScriptTaskServiceTest.class);
    @Autowired
    private ScriptTaskService scriptTaskService;
    @Autowired
    private ScriptTaskRepository scriptTaskRepository;

    @Test
    public void go() throws Exception {
        // 添加一个任务 并验证 失败后会运行失败代码 成功后会运行成功代码
        // 并且最终可以删除
        String name = randomMobile();
        scriptTaskService.submitTask(name, Instant.now().plusMillis(100), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.incrementAndGet()");
        Thread.sleep(1000);
        log.info("checking");
        assertThat(testValue.get())
                .isEqualTo(1);
        // 应该被删除
        assertThat(scriptTaskRepository.countByName(name))
                .isEqualTo(0);

        // 成功 则调用成功代码
        scriptTaskService.submitTask(randomMobile(), Instant.now().plusMillis(100), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.incrementAndGet()", "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.incrementAndGet()");
        Thread.sleep(1000);
        log.info("checking");
        assertThat(testValue.get())
                .isEqualTo(3);

        // 失败 则调用失败代码
        name = randomMobile();
        scriptTaskService.submitTask(name, Instant.now().plusMillis(100), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.hi()", null, "_repository.delete(_this)");
        Thread.sleep(1000);
        log.info("checking");
        assertThat(scriptTaskRepository.countByName(name))
                .isEqualTo(0);
    }

}