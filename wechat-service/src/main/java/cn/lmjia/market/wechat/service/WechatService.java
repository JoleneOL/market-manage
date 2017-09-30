package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Salesman;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信相关时间
 *
 * @author CJ
 */
public interface WechatService {

    /**
     * @param login 用户
     * @return 足够长的临时二维码https地址可以引导新用户关注本公众号并且最终认可是login分享的
     */
//    @Transactional
    String qrCodeForLogin(Login login);

    /**
     * @param salesman 销售人员
     * @return 足够长的临时二维码https地址可以引导新用户关注本公众号并且最终认可是销售人员分享的
     */
//    @Transactional
    String qrCodeFor(Salesman salesman);

    /**
     * loginId分享应用给了openId
     *
     * @param loginId 老用户Id
     * @param openId  微信openId
     * @return openId相关的身份
     */
    @Transactional
    Login shareTo(long loginId, String openId);
}
