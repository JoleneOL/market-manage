package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainDeliverableOrderController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.rows.AgentPrepaymentOrderRows;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.util.ApiDramatizer;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
@Controller
public class WechatGoodAdvanceOrderController extends AbstractMainDeliverableOrderController<AgentPrepaymentOrder> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA);

    @Autowired
    private ConversionService conversionService;

    /**
     * @return 仅仅显示我的订单
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/agentPrepaymentOrderList")
    @RowCustom(distinct = true, dramatizer = ApiDramatizer.class)
    public RowDefinition myOrder(@AuthenticationPrincipal Login login, String search, OrderStatus status) {
        return new AgentPrepaymentOrderRows(t -> t.format(formatter)) {
            @Override
            public Specification<AgentPrepaymentOrder> specification() {
                return new AndSpecification<>(
                        mainDeliverableOrderService.search(search, status)
                        , (root, query, cb) -> cb.equal(root.get("orderBy"), login)
                );
            }
        };
    }

    @GetMapping(SystemService.goodAdvanceOrderList)
    public String list() {
        return "wechat@goodAdvanceOrderList.html";
    }

    @GetMapping("/wechatOrderDetail")
    public String detail(String orderId, Model model) {
        model.addAttribute("order", from(orderId, null));
        return "wechat@orderDetail.html";
    }

    @Override
    protected AgentPrepaymentOrder from(String orderId, Long id) {
        return null;
    }
}
