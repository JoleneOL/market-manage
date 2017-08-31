package cn.lmjia.market.core.service;

import cn.lmjia.market.core.define.MarketUserNoticeType;

/**
 * 微信推送消息助理
 *
 * @author CJ
 */
public interface WechatNoticeHelper {

    /**
     * 注册模板消息
     *
     * @param type        消息类型
     * @param urlTemplate URL模板；可以为null表示不支持url
     */
    void registerTemplateMessage(MarketUserNoticeType type, String urlTemplate);
}
