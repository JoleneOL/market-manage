package cn.lmjia.market.core.service;

import me.jiangcai.wx.WeixinUserService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.UserAccessResponse;
import me.jiangcai.wx.model.WeixinUser;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class EmptyWeixinUserService implements WeixinUserService {

    @Override
    public <T> T userInfo(PublicAccount publicAccount, String s, Class<T> aClass) {
        return null;
    }

    @Override
    public void updateUserToken(PublicAccount publicAccount, UserAccessResponse userAccessResponse) {

    }

    @Override
    public WeixinUser getTokenInfo(PublicAccount account, String openId) {
        throw new IllegalStateException("not support yet");
    }
}
