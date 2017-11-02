package cn.lmjia.market.core.define;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流水
 *
 * @author CJ
 */
public interface Journal {

    /**
     * @return 发生时间
     */
    LocalDateTime getHappenTime();

    /**
     * @return 变化额，正数表示增加，负数表示减少
     */
    BigDecimal getChanged();

    Enum getType();

    /**
     * @return 流水id
     */
    String getId();

    /**
     * @return 相关联的主订单 号id
     */
    Long getMainOrderId();

    Long getAgentPrepaymentOrderId();


    default Money getChangedAbsMoney() {
        return new Money(getChanged().abs());
    }
}
