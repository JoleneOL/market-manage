package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author CJ
 */
@Controller
public class AgentOrderController extends AbstractMainOrderController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ChanpayPaymentForm chanpayPaymentForm;
    @Autowired
    private MainOrderService mainOrderService;

    @GetMapping("/agentOrder")
    public String index(Model model, @AuthenticationPrincipal Login login) {
        orderIndex(login, model, null);
        return "orderPlace.html";
    }

    @PostMapping("/agentOrder")
    public ModelAndView newOrder(HttpServletRequest request, String name, int age, Gender gender, Address address
            , String mobile, long goodId, int amount
            , String activityCode, long recommend, @AuthenticationPrincipal Login login, Model model)
            throws SystemMaintainException {
        MainOrder order = newOrder(login, model, recommend, name, age, gender, address, mobile, goodId, amount
                , activityCode, null);
        HashMap<String, Object> data = new HashMap<>();
        data.put("desktop", true);
        return paymentService.startPay(request, order, chanpayPaymentForm, data);
    }

    @GetMapping("/agentPaySuccess")
    public String paySuccess() {
        return "orderSuccess.html";
    }


}
