package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.service.NoticeService;
import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.user.notice.UserNoticeType;
import me.jiangcai.user.notice.wechat.WechatSendSupplier;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

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
                return "ieAp4pLGQtEE9DZbbAP0_76xNrnjpoHNpQYe2DT8ID0";
            }

            @Override
            public void setTemplateId(String templateId) {

            }
        }, systemService.toUrl("/wechatOrderDetail?orderId={2}"));
    }

    @Override
    public void orderPaySuccess(OrderPaySuccess event) {

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
