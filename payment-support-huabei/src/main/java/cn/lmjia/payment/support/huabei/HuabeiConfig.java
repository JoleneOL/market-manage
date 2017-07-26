package cn.lmjia.payment.support.huabei;

import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * JPA需增配cn.lmjia.payment.support.huabei.entity
 *
 * @author CJ
 */
@Import(PaymentConfig.class)
@Configuration
public class HuabeiConfig {
}
