package cn.lmjia.market.wechat.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.wechat.entity.LimitQRCode;
import cn.lmjia.market.wechat.repository.LimitQRCodeRepository;
import cn.lmjia.market.wechat.service.WechatService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.SceneCode;
import me.jiangcai.wx.protocol.Protocol;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Service
public class WechatServiceImpl implements WechatService {

    @Autowired
    private LimitQRCodeRepository limitQRCodeRepository;
    @Autowired
    private PublicAccount publicAccount;
    @Autowired
    private LoginService loginService;
    @Autowired
    private StandardWeixinUserRepository standardWeixinUserRepository;

    @Override
    public LimitQRCode qrCodeForLogin(Login login) {
        LimitQRCode code = limitQRCodeRepository.findByLogin(login);
        if (code != null)
            return code;
        //最多才 100000看着办吧
        LimitQRCode maxId = limitQRCodeRepository.findTopOrderByIdDesc();
        if (maxId != null && maxId.getId() >= 100000) {
            // 释放掉一个最长时间没用的了
            LimitQRCode newOne = limitQRCodeRepository.findTopOrderByLastUseTimeAsc();
            return forLogin(newOne, login);
        }
        LimitQRCode newOne = new LimitQRCode();
        newOne = limitQRCodeRepository.saveAndFlush(newOne);
        SceneCode sceneCode = Protocol.forAccount(publicAccount).createQRCode(newOne.getId(), null);
        newOne.setImageUrl(sceneCode.getImageURL());
        newOne.setUrl(sceneCode.getUrl());
        return forLogin(newOne, login);
    }

    @Override
    public void shareTo(long loginId, String openId) {
        // 其实应该是这个openId 根本不应该存在！
        // 现在简单点 只要它没有绑定帐号即可
        Login login = loginService.asWechat(openId);
        if (login == null) {
            // 微信
            StandardWeixinUser weixinUser = standardWeixinUserRepository.findByOpenId(openId);
            if (weixinUser == null) {
                weixinUser = new StandardWeixinUser();
                weixinUser.setAppId(publicAccount.getAppID());
                weixinUser.setOpenId(openId);
                weixinUser = standardWeixinUserRepository.save(weixinUser);
            }
            // 创建帐号
            loginService.newLogin(Login.class, loginService.get(loginId), weixinUser);
        }
    }

    private LimitQRCode forLogin(LimitQRCode code, Login login) {
        code.setLogin(login);
        code.setLastUseTime(LocalDateTime.now());
        return code;
    }
}
