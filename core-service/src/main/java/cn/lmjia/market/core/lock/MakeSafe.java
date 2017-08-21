package cn.lmjia.market.core.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by helloztt on 2017-01-09.
 */
@Aspect
public class MakeSafe {

    private static final Log log = LogFactory.getLog(MakeSafe.class);

    @Pointcut("@annotation(cn.lmjia.market.core.lock.BusinessSafe)")
    public void safePoint() {
    }

    @Around("safePoint()")
    public Object aroundSave(ProceedingJoinPoint pjp) throws Throwable {
        // start stopwatch

        final Object lock = toLock(pjp.getArgs());
        log.debug("prepare into lock method:" + pjp.toShortString() + " lock:" + lock);
        synchronized (lock) {
            try {
                log.debug("entering lock method:" + pjp.toShortString());
                return pjp.proceed();
            } finally {
                log.debug("exited lock method:" + pjp.toShortString());
            }
        }
        // stop stopwatch
    }

    private Object toLock(Object[] args) {
        for (Object obj : args) {
            if (obj != null && obj instanceof BusinessLocker) {
                return ((BusinessLocker) obj).toLock();
            }
        }
        return args[0].toString().intern();
    }
}
