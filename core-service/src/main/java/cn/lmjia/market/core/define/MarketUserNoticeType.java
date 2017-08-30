package cn.lmjia.market.core.define;

import me.jiangcai.user.notice.UserNoticeType;
import me.jiangcai.wx.model.message.TemplateMessageParameter;

import java.util.Collection;

/**
 * @author CJ
 */
public interface MarketUserNoticeType extends UserNoticeType {

    /**
     * @return 如果不适用于微信模板消息可以返回null
     */
    Collection<? extends TemplateMessageParameter> parameterStyles();

    /**
     * @return 类型
     */
    MarketNoticeType type();

    @Override
    default String id() {
        return type().getUserNoticeTypeId();
    }
}
