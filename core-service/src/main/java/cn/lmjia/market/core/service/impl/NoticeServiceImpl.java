package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.NoticeService;
import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.user.notice.NoticeChannel;
import me.jiangcai.user.notice.User;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.user.notice.UserNoticeType;
import me.jiangcai.user.notice.wechat.WechatNoticeChannel;
import me.jiangcai.user.notice.wechat.WechatSendSupplier;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private SystemService systemService;
    @Autowired
    private WechatSendSupplier wechatSendSupplier;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private Environment environment;
    @Autowired
    private MainOrderService mainOrderService;

    private boolean useLocal() {
        return environment.acceptsProfiles("staging") || environment.acceptsProfiles(CoreConfig.ProfileUnitTest);
    }

    @PostConstruct
    @Override
    public void init() {

        wechatSendSupplier.registerTemplateMessage(new PaySuccessToOrder(), new TemplateMessageStyle() {
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
            public String getTemplateIdShort() {
                return null;
            }

            @Override
            public String getTemplateTitle() {
                return null;
            }

            @Override
            public String getIndustryId() {
                return null;
            }

            @Override
            public String getTemplateId() {
                return useLocal() ? "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY" : "ieAp4pLGQtEE9DZbbAP0_76xNrnjpoHNpQYe2DT8ID0";
            }

            @Override
            public void setTemplateId(String templateId) {

            }
        }, systemService.toUrl("/wechatOrderDetail?orderId={2}"));

        wechatSendSupplier.registerTemplateMessage(new PaySuccessToJustOrder(), new TemplateMessageStyle() {
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
            public String getTemplateIdShort() {
                return null;
            }

            @Override
            public String getTemplateTitle() {
                return null;
            }

            @Override
            public String getIndustryId() {
                return null;
            }

            @Override
            public String getTemplateId() {
                return useLocal() ? "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY" : "ieAp4pLGQtEE9DZbbAP0_76xNrnjpoHNpQYe2DT8ID0";
            }

            @Override
            public void setTemplateId(String templateId) {

            }
        }, systemService.toUrl("/wechatOrderDetail?orderId={2}"));
    }

    @Override
    public void orderPaySuccess(OrderPaySuccess event) {
        // 前提是 该用户绑定了微信
        MainOrder order = (MainOrder) event.getPayableOrder();
        WeixinUserDetail detail = order.getOrderBy().getWechatUser();
        if (detail != null) {
            // 需要确保收益者和下单人是同一个人
            userNoticeService.sendMessage(null, new User() {
                        @Override
                        public boolean supportNoticeChannel(NoticeChannel channel) {
                            return channel == WechatNoticeChannel.templateMessage;
                        }

                        @Override
                        public Map<String, Object> channelCredential(NoticeChannel channel) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(WechatNoticeChannel.OpenIdCredentialTo, detail.getOpenId());
                            return map;
                        }
                    }, null
                    , mainOrderService.getEnjoyability(order).equals(order.getOrderBy()) ? new PaySuccessToOrder()
                            : new PaySuccessToJustOrder(), new Date(), order.getId(), order.getSerialId()
                    , order.getOrderProductName(), order.getOrderDueAmount());
        }
    }


    /**
     * 区别是 并非订单收益者
     */
    private class PaySuccessToJustOrder extends PaySuccessToOrder {
        @Override
        public String title() {
            return super.title() + "（非收益者）";
        }
    }

    private class PaySuccessToOrder implements UserNoticeType {

        @Override
        public String id() {
            return getClass().getSimpleName();
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
