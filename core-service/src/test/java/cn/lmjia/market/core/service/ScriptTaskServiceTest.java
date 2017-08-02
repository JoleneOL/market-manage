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
        scriptTaskService.submitTask(name, Instant.now(), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.set(1)");
        Thread.sleep(2000);
        log.info("checking");
        assertThat(testValue.get())
                .isEqualTo(1);
        // 应该被删除
        assertThat(scriptTaskRepository.countByName(name))
                .isEqualTo(0);

        // 成功 则调用成功代码
        scriptTaskService.submitTask(randomMobile(), Instant.now(), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.set(2)", "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.incrementAndGet()");
        Thread.sleep(2000);
        log.info("checking");
        assertThat(testValue.get())
                .isEqualTo(3);

        // 失败 则调用失败代码
        name = randomMobile();
        scriptTaskService.submitTask(name, Instant.now(), "Packages.cn.lmjia.market.core.service.ScriptTaskServiceTest.testValue.hi()", null, "_repository.delete(_this)");
        Thread.sleep(2000);
        log.info("checking");
        assertThat(scriptTaskRepository.countByName(name))
                .isEqualTo(0);

        // 其他脚本测试
//        scriptTaskService.submitTask(randomMobile(), Instant.now(), "context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).submitOrderInfo(\"duNYPooMyL\",2,\"W客户gJpvzb\",\"434202153868630178\",\"13092846848\",\"hzts02\",\"台式净水器\",8,\"25440.00\",\"GaLH省-TPoy市-YwLa区-其他地址iKoHhOQPSe\",\"2017-07-30 21:36:06\",0)");
//        Thread.sleep(1000);
//        scriptTaskService.submitTask(randomMobile(),Instant.now(),"context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).deliverUpdate(2,\"pFOgceQJmz\",\"zhwMFrzDVt\",\"ABbRflmcPZ\",23,\"2017-08-01\",\"2017-08-01\",\"W客户nshbVy\",\"14400374041\",\"nLKu省-DYiy市-yRdu区-其他地址HyURdzLKzz\",\"2017-08-01 01:45:26\")");
//        Thread.sleep(2000);
        scriptTaskService.submitTask(randomMobile(), Instant.now(), "context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).deliverUpdate(3,\"TRJ150157118477021574\",\"我家\",\"你家\",100,\"2017-08-03\",\"2017-08-04\",\"测试用户9LDK2\",\"13588049855\",\"浙江省-杭州市-滨江区-地址V59R0\",\"2017-08-03 00:43:07\")");
        Thread.sleep(2000);
    }

}