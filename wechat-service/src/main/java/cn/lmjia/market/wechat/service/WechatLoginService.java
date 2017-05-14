package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.entity.Login;

/**
 * 微信登录服务
 *
 * @author CJ
 */
public interface WechatLoginService {


    Login asWechat(String openId);

}
