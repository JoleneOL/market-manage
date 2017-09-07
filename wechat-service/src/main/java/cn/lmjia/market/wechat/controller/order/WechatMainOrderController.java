package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import cn.lmjia.market.core.service.ChannelService;
import cn.lmjia.market.core.service.PayAssistanceService;
import cn.lmjia.market.core.service.PayService;
import cn.lmjia.market.core.service.SalesmanService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.hua.huabei.entity.HuaHuabeiPayOrder;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CJ
 */
@Controller
public class WechatMainOrderController extends AbstractMainOrderController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private QRController qrController;
    @Autowired
    private PayAssistanceService payAssistanceService;
    @Autowired
    private PayService payService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private SalesmanService salesmanService;

    /**
     * @return 展示下单页面
     */
    @GetMapping(SystemService.wechatOrderURiHB)
    public String indexForHB(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("trj", false);
        model.addAttribute("huabeiEnable", true);
        orderIndex(login, model, null);
        return "wechat@orderPlace.html";
    }

    /**
     * @return 展示下单页面
     */
    @GetMapping(SystemService.wechatOrderURi)
    public String index(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("trj", false);
        model.addAttribute("huabeiEnable", false);
        orderIndex(login, model, null);
        return "wechat@orderPlace.html";
    }

//    @GetMapping("/_pay/{id}")
//    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, @PathVariable("id") long id)
//            throws SystemMaintainException {
//        return payOrder(openId, request, from(null, id));
//    }

    /**
     * @return 展示下单页面
     */
    @GetMapping(TRJEnhanceConfig.TRJOrderURI)
    public String indexForTRJ(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("trj", true);
        model.addAttribute("huabeiEnable", false);
        final Channel channel = channelService.findByName(TRJService.ChannelName);
        if (channel == null)
            throw new IllegalStateException("必要的分期没有被设置。");
        orderIndex(login, model, channel);
        return "wechat@orderPlace.html";
    }

    @GetMapping("/wechatOrderPay")
    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, String orderId)
            throws SystemMaintainException {
        final MainOrder order = from(orderId, null);
        return payAssistanceService.payOrder(openId, request, order, order.isHuabei());
    }

    @GetMapping("/wechatOrderDetail")
    public String detail(String orderId, Model model) {
        model.addAttribute("order", from(orderId, null));
        return "wechat@orderDetail.html";
    }

    // name=%E5%A7%93%E5%90%8D&age=99&gender=2
    // &address=%E6%B5%99%E6%B1%9F%E7%9C%81+%E6%9D%AD%E5%B7%9E%E5%B8%82+%E6%BB%A8%E6%B1%9F%E5%8C%BA
    // &fullAddress=%E6%B1%9F%E7%95%94%E6%99%95%E5%95%A6&mobile=18606509616&goodId=2&leasedType=hzts02&amount=0&activityCode=xzs&recommend=2
    @PostMapping("/wechatOrder")
    @Transactional
    public ModelAndView newOrder(Long salesAchievementId, @OpenId String openId, HttpServletRequest request
            , String name, Gender gender
            , Address address, String mobile, String activityCode, @AuthenticationPrincipal Login login, Model model
            , @RequestParam(required = false) Long channelId
            , String authorising, String idNumber, boolean installmentHuabai, String[] goods)
            throws SystemMaintainException, InvalidAuthorisingException {
        int age = 20;
        MainGoodsAndAmounts amounts = MainGoodsAndAmounts.ofArray(goods);
        MainOrder order = newOrder(login, model, login.getId(), name, age, gender, address, mobile,
                activityCode, channelId, amounts);
        if (salesAchievementId != null) {
            SalesAchievement achievement = salesmanService.getAchievement(salesAchievementId);
            achievement.setCurrentRate(achievement.getWhose().getSalesRate());
            order.setSalesAchievement(achievement);
            achievement.setMainOrder(order);
        }
        if (channelId != null) {
            Channel channel = channelService.get(channelId);
            //        if (!StringUtils.isEmpty(authorising) && !StringUtils.isEmpty(idNumber))
            if (channel.getName().equals(TRJService.ChannelName)) {
                return payAssistanceService.payOrder(openId, request, order, authorising, idNumber);
            }
        }
        order.setHuabei(installmentHuabai);

        return payAssistanceService.payOrder(openId, request, order, installmentHuabai);
    }

    @GetMapping("/_pay/paying")
    @Transactional(readOnly = true)
    public String paying(@RequestHeader(required = false, value = "User-Agent") String agent, String payableOrderId
            , long payOrderId, String checkUri
            , String successUri, Model model) {
        final PayOrder payOrder = paymentService.payOrder(payOrderId);
        String qrCodeUrl;
        String scriptCode;
        // 微信环境下是否友好支付
        boolean wechatFriendly;
        if (payOrder instanceof TRJPayOrder) {
            return "redirect:/wechatPaySuccess";
        } else if (payOrder instanceof HuaHuabeiPayOrder) {
            // 这个url 应该开放权限；在微信场景渲染pay.html；在非微信场景下 直接跳转这个支付地址。
            // MicroMessenger
            if (!StringUtils.isEmpty(agent) && !agent.contains("MicroMessenger")) {
                return "redirect:" + ((HuaHuabeiPayOrder) payOrder).getAliPayCodeUrl();
            }
            qrCodeUrl = qrController.urlForText(((HuaHuabeiPayOrder) payOrder).getAliPayCodeUrl()).toString();
            scriptCode = null;
            wechatFriendly = false;
        } else if (payOrder instanceof ChanpayPayOrder) {
            qrCodeUrl = ((ChanpayPayOrder) payOrder).getUrl();
            scriptCode = null;
            wechatFriendly = true;
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
            wechatFriendly = true;
        } else if (payOrder.isTestOrder()) {
            qrCodeUrl = "";
            scriptCode = "";
            wechatFriendly = true;
        } else
            throw new IllegalStateException("尚未支持扫码的支付系统");

        model.addAttribute("order", payService.getOrder(payableOrderId));
//        model.addAttribute("payOrder", payOrder);
        model.addAttribute("qrCodeUrl", qrCodeUrl);
        model.addAttribute("scriptCode", scriptCode);
        model.addAttribute("checkUri", checkUri);
        model.addAttribute("successUri", successUri);
        model.addAttribute("wechatFriendly", wechatFriendly);
        if (scriptCode != null)
            return "wechat@payWithJS.html";
        return "wechat@pay.html";
    }

    @GetMapping("/wechatPaySuccess")
    public String paySuccess() {
        return "wechat@orderSuccess.html";
    }

}
