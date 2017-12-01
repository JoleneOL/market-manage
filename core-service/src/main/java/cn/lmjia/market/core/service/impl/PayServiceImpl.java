package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.repository.PayOrderRepository;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.service.*;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import lombok.SneakyThrows;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author CJ
 */
@Service
public class PayServiceImpl implements PayService {

    private static final Log log = LogFactory.getLog(PayServiceImpl.class);

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private PayOrderRepository payOrderRepository;
    @Autowired
    private PromotionRequestRepository promotionRequestRepository;
    @Autowired
    private PromotionRequestService promotionRequestService;
    @Autowired
    private WechatNoticeHelper wechatNoticeHelper;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private CommissionDetailService commissionDetailService;
    @Autowired
    private LoginService loginService;

    @Override
    public String mainOrderPaySuccessUri(HttpServletRequest request, MainOrder order, PayOrder payOrder) {
        if (!WeixinWebSpringConfig.isWeixinRequest(request)) {
            if (payOrder == null)
                return "/agentPaySuccess?mainOrderId=" + order.getId();
            return "/agentPaySuccess?mainOrderId=" + order.getId() + "&payOrderId=" + payOrder.getId();
        }
        if (payOrder == null)
            return "/wechatPaySuccess?mainOrderId=" + order.getId();
        return "/wechatPaySuccess?mainOrderId=" + order.getId() + "&payOrderId=" + payOrder.getId();
    }

    @Override
    public ModelAndView paySuccess(HttpServletRequest request, PayableOrder payableOrder, PayOrder payOrder) {
        if (payableOrder instanceof MainOrder) {
            MainOrder mainOrder = (MainOrder) payableOrder;
            return new ModelAndView("redirect:" + mainOrderPaySuccessUri(request, mainOrder, payOrder));
        } else if (payableOrder instanceof PromotionRequest) {
            return new ModelAndView("redirect:/wechatUpgradeApplySuccess");
        } else
            throw new IllegalStateException("暂时不支持：" + payableOrder);
    }

    @Override
    @SneakyThrows
    public ModelAndView pay(HttpServletRequest request, PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters) {
        if (!WeixinWebSpringConfig.isWeixinRequest(request)) {
            // 非微信下是直接跳转到收银台
            ChanpayPayOrder chanpayPayOrder = (ChanpayPayOrder) payOrder;
            return new ModelAndView("redirect:" + chanpayPayOrder.getUrl());
        }
        return new ModelAndView("redirect:/_pay/paying?payableOrderId=" + order.getPayableOrderId() + "&payOrderId="
                + payOrder.getId());
        //"&checkUri="
//        + URLEncoder.encode(additionalParameters.get("checkUri").toString(), "UTF-8") + "&successUri="
//                + URLEncoder.encode(additionalParameters.get("successUri").toString(), "UTF-8")
    }

    @Override
    public boolean isPaySuccess(String id) {
        // 前面的main-去去掉
        PayableOrder order = getOrder(id);
        if (order instanceof MainOrder)
            return mainOrderService.isPaySuccess(((MainOrder) order).getId());
        if (order instanceof PromotionRequest)
            return ((PromotionRequest) order).getPaymentStatus() == PaymentStatus.payed;
        throw new IllegalArgumentException("不支持的订单类型：" + order.getClass());
    }

    @Override
    public PayableOrder getOrder(String id) {
        Long orderId = MainOrder.payableOrderIdToId(id);
        if (orderId != null)
            return mainOrderService.getOrder(orderId);
        orderId = PromotionRequest.payableOrderIdToId(id);
        if (orderId != null)
            return promotionRequestRepository.getOne(orderId);
        throw new IllegalArgumentException("不支持的可支付ID:" + id);
    }

    @Override
    @EventListener(OrderPaySuccess.class)
    public void paySuccess(OrderPaySuccess event) {
        log.info("处理付款成功事件");
        if (event.getPayableOrder() instanceof MainOrder) {
            MainOrder mainOrder = (MainOrder) event.getPayableOrder();
            if (mainOrder.isPay()) {
                log.warn("订单已支付，却发起了重复事件。" + mainOrder.getSerialId());
                return;
            }
//                throw new IllegalStateException("订单已支付");
            mainOrder.setPayTime(LocalDateTime.now());
            mainOrder.setOrderStatus(OrderStatus.forDeliver);
            mainOrder.setPayOrder(event.getPayOrder());
            //支付成功,要将产生的佣金提醒直接推荐人或促销人员,判断是否曾经支付过订单.
            CommissionForPeople commissionForPeople = new CommissionForPeople();
            wechatNoticeHelper.registerTemplateMessage(commissionForPeople, null);
            Login login = mainOrder.getOrderBy();
            try {
                if (login.isSuccessOrder()) {
                    //身份是会员,给他自己和直接推荐人或者促销人员发送模版消息.
                    List<Commission> commissionList = commissionDetailService.findByOrderId(mainOrder.getId());
                    BigDecimal oneselfAmount = new BigDecimal(0);
                    BigDecimal guideUserAmount = new BigDecimal(0);
                    for (Commission commission : commissionList) {
                        if (commission.getWho().equals(login)) {
                            //属于自己的所有佣金
                            oneselfAmount = oneselfAmount.add(commission.getAmount());
                        }
                        //是否是他的推荐人
                        if (commission.getWho().equals(login.getGuideUser())) {
                            guideUserAmount = guideUserAmount.add(commission.getAmount());
                        }
                    }
                    userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(login)), null, commissionForPeople, "￥" + oneselfAmount.toString(), new Date());
                    userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(login.getGuideUser())), null, commissionForPeople, "￥" + guideUserAmount.toString(), new Date());
                } else {
                    //连爱心天使都不是,第一次下单,这时候仅仅向他的推荐人发送消息
                    List<Commission> commissionList = commissionDetailService.findByOrderId(mainOrder.getId());
                    BigDecimal guideUserAmount = new BigDecimal(0);
                    ;
                    for (Commission commission : commissionList) {
                        //是否是他的推荐人
                        if (commission.getWho().equals(login.getGuideUser())) {
                            guideUserAmount = guideUserAmount.add(commission.getAmount());
                        }
                    }
                    userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(login.getGuideUser())), null, commissionForPeople, "￥" + guideUserAmount.toString(), new Date());
                }
            } catch (Exception e) {
                log.error("发送佣金提醒失败" + e.getMessage());
            }
        } else if (event.getPayableOrder() instanceof PromotionRequest) {
            PromotionRequest request = (PromotionRequest) event.getPayableOrder();
            request.setPaymentStatus(PaymentStatus.payed);
            request.setPayTime(LocalDateTime.now());
            promotionRequestService.submitRequest(request);
        } else
            throw new IllegalStateException("暂时不支持：" + event.getPayableOrder());
//        mainOrderRepository.save(mainOrder);
    }

    @Override
    @EventListener(OrderPayCancellation.class)
    public void payCancel(OrderPayCancellation event) {
        log.info(event.getPayableOrder() + "放弃了支付");
        payOrderRepository.delete(event.getPayOrder());
    }

    /**
     * 通知佣金直接获取人消息模版
     */
    private class CommissionForPeople implements MarketUserNoticeType {

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "恭喜您,获得了一笔新的佣金。")
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
                    String.class,// 0佣金金额
                    Date.class, // 1获得的时间
            };
        }
    }
}
