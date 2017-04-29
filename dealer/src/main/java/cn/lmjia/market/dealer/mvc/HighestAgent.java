package cn.lmjia.market.dealer.mvc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该值标注一个MVC方法的参数上；可以解析为当前登录者所对应的最高代理级别
 *
 * @author CJ
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HighestAgent {
}
