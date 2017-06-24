/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 *
 */

package cn.lmjia.market.core.service;

import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.user.notice.BusinessOwner;
import me.jiangcai.user.notice.NoticeChannel;
import me.jiangcai.user.notice.singleton.SingletonBusinessOwner;
import me.jiangcai.user.notice.wechat.WechatNoticeChannel;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.TokenType;
import me.jiangcai.wx.model.PublicAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的公众号
 *
 * @author CJ
 */
@Component
public class MyPublicAccount extends PublicAccount implements PublicAccountSupplier, SingletonBusinessOwner, BusinessOwner {

    private static final String AccessToken = "huotao.weixin.accessToken";
    private static final String JavascriptTicket = "huotao.weixin.javascriptTicket";
    private static final String TimeToExpire = "huotao.weixin.timeToExpire";
    private static final String JavascriptTimeToExpire = "huotao.weixin.javascriptTimeToExpire";
    private final SystemStringService systemStringService;
    private final Map<String, Object> channelCredential = new HashMap<>();

    {
        channelCredential.put(WechatNoticeChannel.PublicAccountCredentialFrom, this);
    }

    @Autowired
    public MyPublicAccount(SystemStringService systemStringService, Environment environment) {
        this.systemStringService = systemStringService;
        setAppID(environment.getRequiredProperty("huotao.weixin.appId"));
        setAppSecret(environment.getRequiredProperty("huotao.weixin.appSecret"));
        setInterfaceURL(environment.getRequiredProperty("huotao.weixin.url"));
        setInterfaceToken(environment.getRequiredProperty("huotao.weixin.token"));
        //
        setAccessToken(systemStringService.getSystemString(AccessToken, String.class, null));
        setJavascriptTicket(systemStringService.getSystemString(JavascriptTicket, String.class, null));
        setTimeToExpire(systemStringService.getSystemString(TimeToExpire, LocalDateTime.class, null));
        setJavascriptTimeToExpire(systemStringService.getSystemString(JavascriptTimeToExpire, LocalDateTime.class, null));
    }

    @Override
    public List<PublicAccount> getAccounts() {
        return Collections.singletonList(this);
    }

    @Override
    public PublicAccount findByIdentifier(String identifier) {
        return this;
    }

    @Override
    public void updateToken(PublicAccount account, TokenType type, String token, LocalDateTime timeToExpire) throws Throwable {
        if (type == TokenType.access) {
            systemStringService.updateSystemString(AccessToken, token);
            systemStringService.updateSystemString(TimeToExpire, timeToExpire);
        } else if (type == TokenType.javascript) {
            systemStringService.updateSystemString(JavascriptTicket, token);
            systemStringService.updateSystemString(JavascriptTimeToExpire, timeToExpire);
        }
    }

    @Override
    public void getTokens(PublicAccount account) {

    }

    @Override
    public PublicAccount findByHost(String host) {
        return this;
    }

    @Override
    public PublicAccountSupplier getSupplier() {
        return this;
    }

    @Override
    public BusinessOwner globalBusinessOwner() {
        return this;
    }

    @Override
    public String id() {
        return "_";
    }

    @Override
    public boolean supportNoticeChannel(NoticeChannel channel) {
        return channel == WechatNoticeChannel.templateMessage;
    }

    @Override
    public Map<String, Object> channelCredential(NoticeChannel channel) {
        return channelCredential;
    }
}
