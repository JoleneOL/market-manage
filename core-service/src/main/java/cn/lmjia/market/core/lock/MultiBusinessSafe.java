package cn.lmjia.market.core.lock;

import java.lang.annotation.*;

/**
 * 多重锁
 * Created by helloztt on 2017-01-09.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiBusinessSafe {
}
