package cn.lmjia.market.core.define;

import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * 拒绝合伙人升级申请
 */
public class PromotionRequestRejected implements MarketUserNoticeType {


    @Override
    public Collection<? extends TemplateMessageParameter> parameterStyles() {
        return Arrays.asList(
                new SimpleTemplateMessageParameter("first", "您的升级申请已被拒绝。")
                , new SimpleTemplateMessageParameter("keyword1", "{0}")
                , new SimpleTemplateMessageParameter("keyword2", "{1}")
                , new SimpleTemplateMessageParameter("keyword3", "{2}")
                , new SimpleTemplateMessageParameter("remark", "欢迎满足条件后再次申请。")
        );
    }

    @Override
    public MarketNoticeType type() { return MarketNoticeType.PromotionRequestRejected; }

    @Override
    public String title() {
        return null;
    }

    @Override
    public boolean allowDifferentiation() {
        return true;
    }

    @Override
    public String defaultToText(Locale locale, Object[] parameters) { return "您的申请已经被拒绝"; }

    @Override
    public String defaultToHTML(Locale locale, Object[] parameters) { return "您的申请已经被拒绝"; }

    @Override
    public Class<?>[] expectedParameterTypes() {
        return new Class<?>[]{
                String.class,//审核项目 0
                Date.class, //下单时间 1
                String.class //审核结果 2
        };
    }
}
