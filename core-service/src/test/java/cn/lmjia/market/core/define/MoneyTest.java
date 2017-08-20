package cn.lmjia.market.core.define;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MoneyTest {

    @Test
    public void go() {
        Money money = new Money(BigDecimal.valueOf(new Random().nextDouble()).multiply(BigDecimal.valueOf(10000)));
        System.out.println(money.toString());
        money = new Money(new BigDecimal("2820.00000000000000000000"));
        System.out.println(money.toString());
        assertThat(true)
                .isTrue();
    }

}