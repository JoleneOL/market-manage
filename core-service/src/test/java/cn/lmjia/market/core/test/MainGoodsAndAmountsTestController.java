package cn.lmjia.market.core.test;

import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author CJ
 */
@Controller
public class MainGoodsAndAmountsTestController {

    private static final Log log = LogFactory.getLog(MainGoodsAndAmountsTestController.class);

    @PostMapping("/MainGoodsAndAmountsTestController")
    @ResponseBody
    public String model(String[] goods) {
        MainGoodsAndAmounts amounts = MainGoodsAndAmounts.ofArray(goods);
        log.info("goods:" + amounts);
        return amounts.toString();
//        log.info("goods:" + Arrays.toString(goods));
//        return Arrays.toString(goods);
    }

}
