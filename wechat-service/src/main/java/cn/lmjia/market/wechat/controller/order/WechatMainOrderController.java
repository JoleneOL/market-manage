package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.paymax.PaymaxChannel;
import me.jiangcai.payment.paymax.PaymaxPaymentForm;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
    private PaymaxPaymentForm paymaxPaymentForm;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private Environment environment;
    @Autowired
    private QRController qrController;

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
    public ModelAndView newOrder(@OpenId String openId, HttpServletRequest request, String name, int age, Gender gender, Address address, String mobile, long goodId, int amount
            , String activityCode, long recommend, @AuthenticationPrincipal Login login, Model model) throws SystemMaintainException {
        MainOrder order = newOrder(login, model, recommend, name, age, gender, address, mobile, goodId, amount, activityCode);
        if (environment.acceptsProfiles("wechatChanpay"))
            return paymentService.startPay(request, order, chanpayPaymentForm, null);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("channel", PaymaxChannel.wechatScan);
        parameters.put("openId", openId);
        return paymentService.startPay(request, order, paymaxPaymentForm, parameters);
    }

    @GetMapping("/wechatPaying")
    @Transactional(readOnly = true)
    public String paying(long mainOrderId, long payOrderId, String checkUri
            , String successUri, Model model) {
        final PayOrder payOrder = paymentService.payOrder(payOrderId);
        String qrCodeUrl;
        if (payOrder instanceof ChanpayPayOrder)
            qrCodeUrl = ((ChanpayPayOrder) payOrder).getUrl();
        else if (payOrder instanceof PaymaxPayOrder) {
            // 我们自己写了一个控制器 可以让一个地址变成一个二维码
            qrCodeUrl = qrController.urlForText(((PaymaxPayOrder) payOrder).getScanUrl()).toString();
        } else
            throw new IllegalStateException("尚未支持扫码的支付系统");

        model.addAttribute("order", mainOrderService.getOrder(mainOrderId));
//        model.addAttribute("payOrder", payOrder);
        model.addAttribute("qrCodeUrl", qrCodeUrl);
        model.addAttribute("checkUri", checkUri);
        model.addAttribute("successUri", successUri);
        return "wechat@pay.html";
    }

    @GetMapping("/wechatPaySuccess")
    public String paySuccess(long mainOrderId) {
        return "wechat@orderSuccess.html";
    }

}
