package cn.lmjia.market.core.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by helloztt on 2017-01-09.
 */
@Aspect
public class MakeSafe {

    private static final Log log = LogFactory.getLog(MakeSafe.class);

    @Pointcut("@annotation(cn.lmjia.market.core.lock.BusinessSafe)")
    public void safePoint() {
    }

    @Pointcut("@annotation(cn.lmjia.market.core.lock.MultiBusinessSafe)")
    public void multipleSafePoint(){
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

    @Around("multipleSafePoint()")
    public Object multipleAroundSave(ProceedingJoinPoint pjp) throws Throwable {
        final Object[] locks = toMultiLock(pjp.getArgs());
        return multiLock(locks,pjp);
    }

    private Object toLock(Object[] args) {
        for (Object obj : args) {
            if (obj != null && obj instanceof BusinessLocker) {
                return ((BusinessLocker) obj).toLock();
            }
        }
        return args[0].toString().intern();
    }

    private Object[] toMultiLock(Object[] args){
        for(Object obj : args){
            if(obj != null && obj instanceof MultipleBusinessLocker){
                return ((MultipleBusinessLocker) obj).toLock();
            }
        }
        return new Object[]{args[0].toString().intern()};
    }

    private Object multiLock(Object[] locks,ProceedingJoinPoint pjp) throws Throwable {
        if(locks.length == 0){
            synchronized (locks[0]){
                try {
                    log.debug("entering lock method:" + pjp.toShortString());
                    return pjp.proceed();
                } finally {
                    log.debug("exited lock method:" + pjp.toShortString());
                }
            }
        }else{
            Object[] newLocks = new Object[locks.length-1];
            System.arraycopy(locks,1,newLocks,0,newLocks.length);
            multiLock(newLocks,pjp);
        }
        return null;
    }
}
