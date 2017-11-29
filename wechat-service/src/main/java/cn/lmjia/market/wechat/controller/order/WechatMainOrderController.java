package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainOrderController;
import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import cn.lmjia.market.core.service.*;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import cn.lmjia.market.core.trj.TRJService;
import com.alibaba.fastjson.JSONObject;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.hua.huabei.entity.HuaHuabeiPayOrder;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.user.notice.User;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

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
    private MainOrderService mainOrderService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private WechatNoticeHelper wechatNoticeHelper;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private CommissionDetailService commissionDetailService;
    @Autowired
    private LoginService loginService;

    //    @GetMapping("/_pay/{id}")
//    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, @PathVariable("id") long id)
//            throws SystemMaintainException {
//        return payOrder(openId, request, from(null, id));
//    }
    @Autowired
    private ReadService readService;
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

//    @GetMapping("/_pay/{id}")
//    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, @PathVariable("id") long id)
//            throws SystemMaintainException {
//        return payOrder(openId, request, from(null, id));
//    }

    /**
     * 微信支付
     *
     * @param orderId           订单编号，注意！！！
     * @param orderPKId         订单主键，注意！！！
     * @param channelId         渠道
     * @param authorising       按揭码
     * @param idNumber          身份证
     * @param installmentHuabai 是否使用花呗支付
     * @return 支付页面
     * @throws SystemMaintainException     系统维护异常
     * @throws InvalidAuthorisingException 按揭码校验异常
     */
    @GetMapping("/wechatOrderPay")
    public ModelAndView pay(@OpenId String openId, HttpServletRequest request, String orderId, Long orderPKId
            , Long channelId, String authorising, String idNumber, Boolean installmentHuabai)
            throws SystemMaintainException, InvalidAuthorisingException {
        final MainOrder order = from(orderId, orderPKId);
        if (channelId != null) {
            Channel channel = channelService.get(channelId);
            //        if (!StringUtils.isEmpty(authorising) && !StringUtils.isEmpty(idNumber))
            if (channel.getName().equals(TRJService.ChannelName)) {
                return payAssistanceService.payOrder(openId, request, order, authorising, idNumber);
            }
        }
        if (installmentHuabai != null) {
            order.setHuabei(installmentHuabai);
        }
        return payAssistanceService.payOrder(openId, request, order, order.isHuabei());
    }

    @GetMapping("/wechatPayForMainOrder{id}")
    public ModelAndView payForMainOrder(@OpenId String openId, HttpServletRequest request, @PathVariable("id") long id)
            throws SystemMaintainException {
        final MainOrder order = mainOrderService.getOrder(id);
        return payAssistanceService.payOrder(openId, request, order, order.isHuabei());
    }

    @GetMapping("/wechatOrderDetail")
    public String detail(String orderId, Model model) {
        model.addAttribute("order", from(orderId, null));
        return "wechat@orderDetail.html";
    }

    /**
     * 微信下单
     *
     * @param name              客户姓名
     * @param gender            客户性别
     * @param address           地址
     * @param mobile            手机号
     * @param activityCode      可选的按揭识别码
     * @param channelId         渠道
     * @param authorising       按揭码
     * @param idNumber          身份证
     * @param installmentHuabai 是否使用花呗支付
     * @param goods             下单商品
     * @return 创建订单结果
     * @throws MainGoodLowStockException   库存不足异常
     * @throws InvalidAuthorisingException 按揭码校验异常
     */
    // name=%E5%A7%93%E5%90%8D&age=99&gender=2
    // &address=%E6%B5%99%E6%B1%9F%E7%9C%81+%E6%9D%AD%E5%B7%9E%E5%B8%82+%E6%BB%A8%E6%B1%9F%E5%8C%BA
    // &fullAddress=%E6%B1%9F%E7%95%94%E6%99%95%E5%95%A6&mobile=18606509616&goodId=2&leasedType=hzts02&amount=0&activityCode=xzs&recommend=2
    @PostMapping("/wechatOrder")
    @ResponseBody
    @Transactional
    public ApiResult newOrder(@OpenId String openId, HttpServletRequest request, String name, Gender gender
            , Long salesAchievementId
            , Address address, String mobile, String activityCode, @AuthenticationPrincipal Login login, Model model
            , @RequestParam(required = false) Long channelId
            , String authorising, String idNumber, boolean installmentHuabai
            , String[] goods, @RequestParam(name = "goods[]", required = false) String[] goodsArray)
            throws MainGoodLowStockException, InvalidAuthorisingException {
        int age = 20;
        MainGoodsAndAmounts amounts = getMainGoodAndAmounts(goods, goodsArray);

        MainOrder order = newOrder(login, model, login.getId(), name, age, gender, address, mobile,
                activityCode, channelId, amounts);
        JSONObject result = new JSONObject();
        result.put("id", order.getId());
        if (salesAchievementId != null) {
            SalesAchievement achievement = salesmanService.getAchievement(salesAchievementId);
            achievement.setCurrentRate(achievement.getWhose().getSalesRate());
            order.setSalesAchievement(achievement);
            achievement.setMainOrder(order);
        }
        if (channelId != null) {
            //校验按揭码
            payAssistanceService.checkAuthorising(authorising, idNumber);
            result.put("channelId", channelId);
            result.put("idNumber", idNumber);
            result.put("authorising", authorising);
        }
        result.put("installmentHuabai", installmentHuabai);
        return ApiResult.withCode(200, result);
    }

    @GetMapping("/_pay/paying")
    @Transactional(readOnly = true)
    public String paying(@RequestHeader(required = false, value = "User-Agent") String agent, String payableOrderId
            , long payOrderId, String checkUri
            , String successUri, Model model, HttpServletRequest request) {
        final PayOrder payOrder = paymentService.payOrder(payOrderId);
        String qrCodeUrl;
        String scriptCode;
        // 微信环境下是否友好支付
        boolean wechatFriendly;
        if (payOrder instanceof TRJPayOrder) {
            MainOrder order = (MainOrder) payService.getOrder(payableOrderId);
            return "redirect:" + payService.mainOrderPaySuccessUri(request, order, payOrder);
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

        // 如果是个主订单的支付页面，还可以分享让他人支付
        if (MainOrder.payableOrderIdToId(payableOrderId) != null) {
            MainOrder order = (MainOrder) payService.getOrder(payableOrderId);
            // 分享标题  利每家
            // 内容可定制
            MessageFormat mainOrderSharePayContentTemplate = new MessageFormat(systemStringService.getCustomSystemString(
                    "market.mainOrder.sharePay.contentFormat"
                    , "market.mainOrder.sharePay.contentFormat.comment", true, String.class
                    , "{0}请您支付订单:{1}"));
            String name = readService.nameForPrincipal(order.getOrderBy());
            model.addAttribute("shareContent", mainOrderSharePayContentTemplate.format(new Object[]{
                    name, order.getOrderBody()
            }));
            // URL 你懂的
            model.addAttribute("shareUrl", systemService.toUrl("/wechatPayForMainOrder" + order.getId()));
            // 图标LOG 固定的
        }

        if (scriptCode != null)
            return "wechat@payWithJS.html";
        return "wechat@pay.html";
    }

    @GetMapping("/wechatPaySuccess")
    @Transactional(readOnly = true)
    public String paySuccess(@AuthenticationPrincipal Object login, Long mainOrderId, Model model) {
        boolean yours;
        if (mainOrderId == null)
            yours = true;
        else {
            MainOrder order = mainOrderService.getOrder(mainOrderId);
            yours = order.getOrderBy().equals(login);
        }
        //支付成功,要将产生的佣金提醒直接推荐人或促销人员,判断是否曾经支付过订单.
        CommissionForPeople commissionForPeople = new CommissionForPeople();
        wechatNoticeHelper.registerTemplateMessage(commissionForPeople, null);
        try {
            if (((Login) login).isSuccessOrder()) {
                //身份是会员,给他自己和直接推荐人或者促销人员发送模版消息.
                List<Commission> CommissionList = commissionDetailService.findByOrderId(mainOrderId);
                BigDecimal oneselfAmount = new BigDecimal(0);
                BigDecimal guideUserAmount = new BigDecimal(0);
                for (Commission commission : CommissionList) {
                    if (commission.getWho().equals(login)) {
                        //属于自己的所有佣金
                        oneselfAmount = oneselfAmount.add(commission.getAmount());
                    }
                    //是否是他的推荐人
                    if (commission.getWho().equals(((Login) login).getGuideUser())) {
                        guideUserAmount = guideUserAmount.add(commission.getAmount());
                    }
                }
                userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton((Login) login)), null, commissionForPeople, "获取的佣金金额:￥" + oneselfAmount.toString(), new Date());
                userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(((Login) login).getGuideUser())), null, commissionForPeople, "获取的佣金金额:￥" + guideUserAmount.toString(), new Date());
            } else {
                //连爱心天使都不是,第一次下单,这时候仅仅向他的推荐人发送消息
                List<Commission> CommissionList = commissionDetailService.findByOrderId(mainOrderId);
                BigDecimal guideUserAmount = null;
                for (Commission commission : CommissionList) {
                    //是否是他的推荐人
                    if (commission.getWho().equals(((Login) login).getGuideUser())) {
                        guideUserAmount = guideUserAmount.add(commission.getAmount());
                    }
                }
                userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(((Login) login).getGuideUser())), null, commissionForPeople, "获取的佣金金额:￥" + guideUserAmount.toString(), new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("yours", yours);
        return successView(model);
    }

    @Override
    protected String listUri() {
        return "/wechatOrderList";
    }

    @Override
    protected String listTitle() {
        return "订单列表";
    }

    /**
     * 通知佣金直接获取人消息模版
     */
    private class CommissionForPeople implements MarketUserNoticeType {

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "恭喜您,获得了一笔新的佣金.。")
                    , new SimpleTemplateMessageParameter("keyword1", "{0}")
                    , new SimpleTemplateMessageParameter("keyword2", "{1}")
                    , new SimpleTemplateMessageParameter("remark", "感谢您的使用。")
            );
        }

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.CommissionForPeople;
        }

        @Override
        public String title() {
            return null;
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "恭喜您,获得了一笔新的佣金.";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "恭喜您,获得了一笔新的佣金.";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    String.class,//佣金金额
                    Date.class, //获得的时间
            };
        }
    }
}
