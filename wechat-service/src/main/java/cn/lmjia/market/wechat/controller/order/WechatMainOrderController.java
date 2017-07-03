package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.repository.PayOrderRepository;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.paymax.PaymaxChannel;
import me.jiangcai.payment.paymax.PaymaxPaymentForm;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CJ
 */
@Controller
public class WechatMainOrderController extends AbstractMainOrderController {

    private static final Log log = LogFactory.getLog(WechatMainOrderController.class);
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
    @Autowired
    private PayOrderRepository payOrderRepository;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * @return 展示下单页面
     */
    @GetMapping("/wechatOrder")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        orderIndex(login, model);
        return "wechat@orderPlace.html";
    }

    @GetMapping("/wechatOrderPay")
    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, String orderId) throws SystemMaintainException {
        final MainOrder order = from(orderId, null);
        return payOrder(openId, request, order);
    }

//    @GetMapping("/_pay/{id}")
//    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, @PathVariable("id") long id)
//            throws SystemMaintainException {
//        return payOrder(openId, request, from(null, id));
//    }

    @GetMapping("/wechatOrderDetail")
    public String detail(String orderId, Model model) {
        model.addAttribute("order", from(orderId, null));
        return "wechat@orderDetail.html";
    }

    // name=%E5%A7%93%E5%90%8D&age=99&gender=2
    // &address=%E6%B5%99%E6%B1%9F%E7%9C%81+%E6%9D%AD%E5%B7%9E%E5%B8%82+%E6%BB%A8%E6%B1%9F%E5%8C%BA
    // &fullAddress=%E6%B1%9F%E7%95%94%E6%99%95%E5%95%A6&mobile=18606509616&goodId=2&leasedType=hzts02&amount=0&activityCode=xzs&recommend=2
    @PostMapping("/wechatOrder")
    public ModelAndView newOrder(@OpenId String openId, HttpServletRequest request, String name, int age, Gender gender, Address address, String mobile, long goodId, int amount
            , String activityCode, @AuthenticationPrincipal Login login, Model model)
            throws SystemMaintainException {
        MainOrder order = newOrder(login, model, login.getId(), name, age, gender, address, mobile, goodId, amount
                , activityCode);
        return payOrder(openId, request, order);
    }

    private ModelAndView payOrder(String openId, HttpServletRequest request, MainOrder order) throws SystemMaintainException {
        if (order.getOrderStatus() != OrderStatus.forPay)
            throw new IllegalStateException("订单并不在待支付状态");

        if (environment.acceptsProfiles("autoPay")) {
            // 3 秒之后自动付款
            log.warn("3秒之后自动付款:" + order.getSerialId());
            executorService.schedule(()
                            -> paymentService.mockPay(order)
                    , 3, TimeUnit.SECONDS);
        }

        if (environment.acceptsProfiles("wechatChanpay"))
            return paymentService.startPay(request, order, chanpayPaymentForm, null);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("channel", PaymaxChannel.wechat);
        // 单元测试的时候 无法建立公众号付款
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest, "wechatScanOnly")) {
            parameters.put("channel", PaymaxChannel.wechatScan);
        }
        parameters.put("openId", openId);
        return paymentService.startPay(request, order, paymaxPaymentForm, parameters);
    }

    @GetMapping("/_pay/paying")
    @Transactional(readOnly = true)
    public String paying(long mainOrderId, long payOrderId, String checkUri
            , String successUri, Model model) {
        final PayOrder payOrder = paymentService.payOrder(payOrderId);
        String qrCodeUrl;
        String scriptCode;
        if (payOrder instanceof ChanpayPayOrder) {
            qrCodeUrl = ((ChanpayPayOrder) payOrder).getUrl();
            scriptCode = null;
        } else if (payOrder instanceof PaymaxPayOrder) {
            // 我们自己写了一个控制器 可以让一个地址变成一个二维码
            // 公众号支付页面是有点不同的 我们叫它额外JS支付
            final PaymaxPayOrder paymaxPayOrder = (PaymaxPayOrder) payOrder;
            if (paymaxPayOrder.getScanUrl() == null) {
                qrCodeUrl = null;
                scriptCode = paymaxPayOrder.getJavascriptToPay();
            } else {
                qrCodeUrl = qrController.urlForText(paymaxPayOrder.getScanUrl()).toString();
                scriptCode = null;
            }
        } else if (payOrder.isTestOrder()) {
            qrCodeUrl = "";
            scriptCode = "";
        } else
            throw new IllegalStateException("尚未支持扫码的支付系统");

        model.addAttribute("order", mainOrderService.getOrder(mainOrderId));
//        model.addAttribute("payOrder", payOrder);
        model.addAttribute("qrCodeUrl", qrCodeUrl);
        model.addAttribute("scriptCode", scriptCode);
        model.addAttribute("checkUri", checkUri);
        model.addAttribute("successUri", successUri);
        if (scriptCode != null)
            return "wechat@payWithJS.html";
        return "wechat@pay.html";
    }

    @GetMapping("/wechatPaySuccess")
    public String paySuccess(long mainOrderId) {
        return "wechat@orderSuccess.html";
    }

}
