package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.web.WeixinEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author CJ
 */
@Controller
public class WechatMainOrderController extends AbstractMainOrderController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ChanpayPaymentForm chanpayPaymentForm;
    @Autowired
    private MainOrderService mainOrderService;

    /**
     * @return 展示下单页面
     */
    @GetMapping("/wechatOrder")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        orderIndex(login, model);
        return "wechat@orderPlace.html";
    }

    // name=%E5%A7%93%E5%90%8D&age=99&gender=2
    // &address=%E6%B5%99%E6%B1%9F%E7%9C%81+%E6%9D%AD%E5%B7%9E%E5%B8%82+%E6%BB%A8%E6%B1%9F%E5%8C%BA
    // &fullAddress=%E6%B1%9F%E7%95%94%E6%99%95%E5%95%A6&mobile=18606509616&goodId=2&leasedType=hzts02&amount=0&activityCode=xzs&recommend=2
    @PostMapping("/wechatOrder")
    public ModelAndView newOrder(String name, int age, Gender gender, Address address, String mobile, long goodId, int amount
            , String activityCode, long recommend, @AuthenticationPrincipal Login login, Model model) throws SystemMaintainException {
        MainOrder order = newOrder(login, model, recommend, name, age, gender, address, mobile, goodId, amount, activityCode);
        return paymentService.startPay(order, chanpayPaymentForm, null);
    }

    @GetMapping("/paying")
    @Transactional(readOnly = true)
    public String paying(@WeixinEnvironment boolean wx, long mainOrderId, long payOrderId, String checkUri
            , String successUri, Model model) {
        model.addAttribute("order", mainOrderService.getOrder(mainOrderId));
        model.addAttribute("payOrder", paymentService.payOrder(payOrderId));
        model.addAttribute("checkUri", checkUri);
        model.addAttribute("successUri", successUri);
        if (wx)
            return "wechat@pay.html";
        return "pay.html";
    }

    @GetMapping("/paySuccess")
    public String paySuccess(@WeixinEnvironment boolean wx, long mainOrderId) {
        if (wx)
            return "wechat@orderSuccess.html";
        return "orderSuccess.html";
    }

}
