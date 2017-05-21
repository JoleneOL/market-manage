package cn.lmjia.market.dealer.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

    @Autowired
    private ReadService readService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private MainOrderService mainOrderService;

    /**
     * 仅仅处理自己可以管辖的订单
     * 即属于我方代理体系的
     */
    @RequestMapping(method = RequestMethod.GET, value = "/manageableList")
    public RowDefinition manageableList(@AuthenticationPrincipal Login login, String orderId
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId
            , @RequestParam(required = false) LocalDate orderDate, OrderStatus status) {
        return new MainOrderRows() {
            @Override
            public Specification<MainOrder> specification() {
                return mainOrderService.search(orderId, mobile, goodId, orderDate, status);
            }
        };
    }

}
