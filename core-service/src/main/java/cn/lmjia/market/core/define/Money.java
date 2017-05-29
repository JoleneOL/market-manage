package cn.lmjia.market.core.define;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 钱
 * 特指人民币
 *
 * @author CJ
 */
@Data
public class Money implements Serializable {

    public static final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
    private static final long serialVersionUID = -4928092332934155142L;
    private final BigDecimal amount;

    @Override
    public String toString() {
        return format.format(amount);
    }
}
