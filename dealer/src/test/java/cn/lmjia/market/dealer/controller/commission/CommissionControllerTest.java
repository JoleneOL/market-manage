package cn.lmjia.market.dealer.controller.commission;

import org.junit.Test;

/**
 * @author CJ
 */
public class CommissionControllerTest {
    @Test
    public void formatCommonInfo() throws Exception {
        System.out.println(CommissionController.formatCommonInfo("1个厨下净水机 ￥2820.00000000000000000000"));
    }

}