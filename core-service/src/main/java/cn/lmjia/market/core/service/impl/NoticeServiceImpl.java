package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ManagerService;
import cn.lmjia.market.core.service.NoticeService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.WechatNoticeHelper;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * @author CJ
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Log log = LogFactory.getLog(NoticeServiceImpl.class);
    @Autowired
    private SystemService systemService;
    @Autowired
    private WechatNoticeHelper wechatNoticeHelper;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private PromotionRequestService promotionRequestService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ManagerService managerService;

    @PostConstruct
    @Override
    public void init() {
        promotionRequestService.registerNotices(wechatNoticeHelper);

        wechatNoticeHelper.registerTemplateMessage(new PaySuccessToOrder(), systemService.toUrl("/wechatOrderDetail?orderId={2}"));

        wechatNoticeHelper.registerTemplateMessage(new PaySuccessToJustOrder(), systemService.toUrl("/wechatOrderDetail?orderId={2}"));

        wechatNoticeHelper.registerTemplateMessage(new PaySuccessToCS(), null);

        wechatNoticeHelper.registerTemplateMessage(new NewLoginToLogin(), systemService.toUrl(SystemService.wechatMyURi));
    }

    @Override
    public void orderPaySuccess(OrderPaySuccess event) {
        if (event.getPayableOrder() instanceof MainOrder) {
            // 前提是 该用户绑定了微信
            MainOrder order = (MainOrder) event.getPayableOrder();
            WeixinUserDetail detail = order.getOrderBy().getWechatUser();
            if (detail != null) {
                // 需要确保收益者和下单人是同一个人
                userNoticeService.sendMessage(null
                        , loginService.toWechatUser(Collections.singleton(order.getOrderBy())), null
                        , mainOrderService.getEnjoyability(order).equals(order.getOrderBy()) ? new PaySuccessToOrder()
                                : new PaySuccessToJustOrder(), new Date(), order.getId(), order.getSerialId()
                        , order.getOrderProductName(), order.getOrderDueAmount());
            }

            try {
                userNoticeService.sendMessage(null, loginService.toWechatUser(managerService.levelAs(ManageLevel.customerService))
                        , null, new PaySuccessToCS()
                        , Date.from(ZonedDateTime.of(order.getOrderTime(), ZoneId.systemDefault()).toInstant())
                        , order.getId(), order.getSerialId()
                        , order.getOrderBody()
                        , order.getInstallAddress().toString()
                        , "客户已支付订单，请尽快发货"
                        , ""
                );
            } catch (Throwable ex) {
                log.trace("", ex);
            }
        } else if (event.getPayableOrder() instanceof PromotionRequest) {
            PromotionRequest request = (PromotionRequest) event.getPayableOrder();
            WeixinUserDetail detail = request.getWhose().getWechatUser();
            if (detail != null) {
                userNoticeService.sendMessage(null
                        , loginService.toWechatUser(Collections.singleton(request.getWhose())), null
                        , promotionRequestService.getPaySuccessMessage()
                        , detail.getNickname()
                        , request.getId()
                        , request.getOrderDueAmount()
                        , new Date());
            }

            try {
                userNoticeService.sendMessage(null, loginService.toWechatUser(managerService.levelAs(ManageLevel.customerService))
                        , null, new PaySuccessToCS()
                        , Date.from(ZonedDateTime.of(request.getRequestTime(), ZoneId.systemDefault()).toInstant())
                        , request.getId(), String.valueOf(request.getId())
                        , "合伙人升级服务"
                        , "无"
                        , "客户已支付订单，请检查相关资料，并完成合伙人升级"
                        , ""
                );
            } catch (Throwable ex) {
                log.trace("", ex);
            }
        }


    }

    @Override
    public void newLogin(Login login, String mobile) {
        if (login.getGuideUser() != null) {
            userNoticeService.sendMessage(null
                    , loginService.toWechatUser(Collections.singleton(login.getGuideUser())), null
                    , new NewLoginToLogin(), login.getWechatUser().getNickname(), mobile);
        }
    }

    /**
     * String.class// 昵称 0
     * , String.class//手机 1
     */
    private class NewLoginToLogin implements MarketUserNoticeType {

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.NewLoginToLogin;
        }

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "恭喜您，您的团队增加了一位新人。")
                    , new SimpleTemplateMessageParameter("keyword1", "{0}")
                    , new SimpleTemplateMessageParameter("keyword2", "{1}")
                    , new SimpleTemplateMessageParameter("remark", "")
            );
        }

        @Override
        public String title() {
            return "新的合伙人加入给老用户";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "欢迎新合伙人加入";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "欢迎新合伙人加入";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    String.class// 昵称 0
                    , String.class//手机 1
            };
        }
    }

    /**
     * Date.class //时间 0 下单时间
     * , Long.class //id 1
     * , String.class//orderId 2
     * , String.class//orderBody 3
     * , String.class//address 4
     * , String.class//first 5
     * , String.class//first 6
     */
    private class PaySuccessToCS implements MarketUserNoticeType {

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.PaySuccessToCS;
        }

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "{5}")
                    , new SimpleTemplateMessageParameter("keyword1", "{2}")
                    , new SimpleTemplateMessageParameter("keyword2", "{0,date,yyyy-MM-dd HH:mm}")
                    , new SimpleTemplateMessageParameter("keyword3", "{3}")
                    , new SimpleTemplateMessageParameter("keyword4", "{4}")
                    , new SimpleTemplateMessageParameter("remark", "{6}")
            );
        }

        @Override
        public String title() {
            return "支付后的客服通知";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "客户已付款，请尽快配货";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "客户已付款，请尽快配货";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    Date.class //时间 0 下单时间
                    , Long.class //id 1
                    , String.class//orderId 2
                    , String.class//orderBody
                    , String.class//address
                    , String.class//first
                    , String.class//first
            };
        }
    }

    /**
     * 区别是 并非订单收益者
     */
    private class PaySuccessToJustOrder extends PaySuccessToOrder {
        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.PaySuccessToJustOrder;
        }

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "您的订单已成功支付。")
                    , new SimpleTemplateMessageParameter("keyword1", "{3}")
                    , new SimpleTemplateMessageParameter("keyword2", "{2}")
                    , new SimpleTemplateMessageParameter("keyword3", "{4,number,￥,###.##}")
                    , new SimpleTemplateMessageParameter("remark", "谢谢您的惠顾。")
            );
        }

        @Override
        public String title() {
            return super.title() + "（非收益者）";
        }
    }

    private class PaySuccessToOrder implements MarketUserNoticeType {

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.PaySuccessToOrder;
        }

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "您的订单已成功支付。")
                    , new SimpleTemplateMessageParameter("keyword1", "{3}")
                    , new SimpleTemplateMessageParameter("keyword2", "{2}")
                    , new SimpleTemplateMessageParameter("keyword3", "{4,number,￥,###.##}")
                    , new SimpleTemplateMessageParameter("remark", "佣金将在订单完成后到账，现在可以在佣金界面选择「即将获佣」中查看。")
            );
        }

        @Override
        public String title() {
            return "订单支付成功-发送给下单者";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "您的订单已成功支付。";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "您的订单已成功支付。";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    Date.class //时间 0
                    , Long.class //id 1
                    , String.class//orderId 2
                    , String.class//名称 3
                    , BigDecimal.class// 金额 4
            };
        }
    }
}
