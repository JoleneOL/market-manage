package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.Login;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * 订单相关的数据服务
 *
 * @author CJ
 */
@Controller
@RequestMapping("/orderData")
public class OrderDataController {

    /**
     * 仅仅处理自己可以管辖的订单
     * 即属于我方代理体系的
     */
    @RequestMapping(method = RequestMethod.GET, value = "/manageableList")
    public void manageableList(@AuthenticationPrincipal Login login, String orderId
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId, LocalDate orderDate) {
        
    }
}
