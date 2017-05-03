package cn.lmjia.market.core.selection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可定制Row
 *
 * @author CJ
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RowCustom {
    /**
     * @return 装饰器
     */
    Class<? extends RowDramatizer> dramatizer() default DefaultRowDramatizer.class;
}
