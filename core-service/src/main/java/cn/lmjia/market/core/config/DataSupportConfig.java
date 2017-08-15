package cn.lmjia.market.core.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 数据支持配置，运行时也应当载入该配置
 * <p>
 * ok 现在加入一套自身实现的实体监听
 * </p>
 * 需要工作环境提供系统属性
 * <ul>
 * <li>com.huotu.huobanplus.uri 默认http://test.api.open.huobanplus.com:8081/</li>
 * <li>mall.domain 默认51flashmall.com</li>
 * </ul>
 *
 * @author CJ
 * @see TransactionAspectSupport#invokeWithinTransaction(java.lang.reflect.Method, Class, TransactionAspectSupport.InvocationCallback)
 * <p>
 * <p>
 * 这里的复杂度可以写一篇很长的文章
 * http://stackoverflow.com/questions/15616710/spring-transactional-with-aspectj-is-totally-ignored
 * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html#aop-aj-ltw-spring
 * 1 是事务监管的启用方式，是通过代理还是通过Aspect(AOP)
 * 代理: 优点 无需配置立马启用
 * 缺点 无法应用在非public方法，意味着所有的事务方法都必须是public
 * Aspect: 优点 几乎无所不能
 * 缺点 需要特定的启动jvm参数 或者采用编译器植入 但又会增加项目的维护成本
 * 2 代理的类型 只有在选择了代理模式以后启用
 * false 使用Java自带的Proxy 缺点很明显 无法代理类！！以为着几乎所有bean都需要接口引用。
 * true  使用CGLIB 性能优秀
 * PROXY false 接口声明即可使用事务
 * 可以通过断点在TransactionAspectSupport的invokeWithinTransaction
 * </p>
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
//@EnableLoadTimeWeaving
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
class DataSupportConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
//        taskRegistrar.addTriggerTask(
//                new Runnable() {
//                    public void run() {
//                        myTask().work();
//                    }
//                },
//                new CustomTrigger()
//        );
    }

//    @Autowired
//    private

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return new ScheduledThreadPoolExecutor(10);
    }
//
//    @Bean
//    public MyTask myTask() {
//        return new MyTask();
//    }

    //    @Autowired
//    @Resource
//    private EntityManagerFactory entityManagerFactory;

//    @Bean
//    public AuditorAware<PlatformUser> auditorProvider() {
//        return () -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication == null || !authentication.isAuthenticated()) {
//                return null;
//            }
//
//            return ((PlatformUser) authentication.getPrincipal());
//        };
//    }
//    @Autowired
//    private DataSource dataSource;

//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager manager = new JpaTransactionManager();
//        manager.setEntityManagerFactory(entityManagerFactory);
//        return manager;
//    }

//    @Bean
//    public DataSourceTransactionManager dataSourceTransactionManager(){
//        return new DataSourceTransactionManager(dataSource);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return transactionManager();
//    }
}
