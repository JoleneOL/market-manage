package cn.lmjia.market.core.aop;

import java.lang.annotation.*;

/**
 * 声明为业务安全的方法，如果参数中有 {@link BusinessLocker}则使用其功能，否者默认使用第一个参数作为业务锁
 * Created by helloztt on 2017-01-09.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessSafe {
}
