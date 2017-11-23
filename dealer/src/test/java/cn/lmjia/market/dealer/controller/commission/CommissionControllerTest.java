package cn.lmjia.market.dealer.controller.commission;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author CJ
 */
public class CommissionControllerTest {
    @Test
    public void formatCommonInfo() throws Exception {
        System.out.println(CommissionController.formatCommonInfo("1个厨下净水机 ￥2820.00000000000000000000"));
        NumberFormat format = NumberFormat.getPercentInstance(Locale.CHINA);
        format.setMaximumFractionDigits(2);
        System.out.println(format.format(BigDecimal.valueOf(0.05)));
        System.out.println(format.format(BigDecimal.valueOf(0.005)));
        System.out.println(format.format(BigDecimal.valueOf(0.0005)));
    }

}