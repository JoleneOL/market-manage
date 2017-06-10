package cn.lmjia.market.core.util;

import java.math.BigDecimal;

/**
 * 分佣的来源
 *
 * @author CJ
 */
public interface CommissionSource {

    /**
     * @return 可被分佣的金额
     */
    BigDecimal getCommissioningAmount();

}
