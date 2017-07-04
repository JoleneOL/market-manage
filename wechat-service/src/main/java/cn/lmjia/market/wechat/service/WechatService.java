package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.entity.LimitQRCode;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信相关时间
 *
 * @author CJ
 */
public interface WechatService {

    /**
     * @param login 用户
     * @return 给这个用户创建永久引入场景二维码
     */
    @Transactional
    LimitQRCode qrCodeForLogin(Login login);

    /**
     * loginId分享应用给了openId
     *
     * @param loginId 老用户Id
     * @param openId  微信openId
     */
    @Transactional
    void shareTo(long loginId, String openId);
}
