package cn.lmjia.market.core.trj;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 投融家相关配置
 *
 * @author CJ
 */
@Configuration
@ComponentScan(
        {"cn.lmjia.market.core.trj.service", "cn.lmjia.market.core.trj.controller"}
)
public class TRJEnhanceConfig {

    /**
     * 投融家订单的下单地址
     */
    public static final String TRJOrderURI = "/wechatTRJOrder";
    public static final String SS_PriceKey = "trj.order.price";

}
