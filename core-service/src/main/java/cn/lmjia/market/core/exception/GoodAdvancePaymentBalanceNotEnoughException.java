package cn.lmjia.market.core.exception;

import cn.lmjia.market.core.define.Money;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 代理商预付货款不足
 *
 * @author CJ
 */
@Getter
public class GoodAdvancePaymentBalanceNotEnoughException extends RuntimeException {


    private final BigDecimal current;
    private final BigDecimal required;

    public GoodAdvancePaymentBalanceNotEnoughException(BigDecimal required, BigDecimal current) {
        this.required = required;
        this.current = current;
    }

    @Override
    public String getMessage() {
        return "余额不足，所需:" + new Money(required).toString() + "，当前：" + new Money(current).toString();
    }
}
