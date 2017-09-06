package cn.lmjia.market.wechat.service;

import cn.lmjia.market.wechat.entity.LimitQRCode;
import cn.lmjia.market.wechat.repository.LimitQRCodeRepository;
import me.jiangcai.wx.classic.TempSceneReply;
import me.jiangcai.wx.message.Message;
import me.jiangcai.wx.model.PublicAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Component
public class AutoShare extends TempSceneReply {

    @Autowired
    private LimitQRCodeRepository limitQRCodeRepository;
    @Autowired
    private WechatService wechatService;

    @Override
    public void happen(PublicAccount account, Message message, int sceneId) {
        LimitQRCode code = limitQRCodeRepository.getOne(sceneId);
        code.setLastUseTime(LocalDateTime.now());
        limitQRCodeRepository.save(code);

        wechatService.shareTo(code.getLogin().getId(), message.getFrom());
    }

    @Override
    public void happen(PublicAccount account, Message message, String sceneStr) {
        if (!StringUtils.isEmpty(sceneStr) && sceneStr.startsWith("SF_")) {
            long loginId = NumberUtils.parseNumber(sceneStr.substring(3), Long.class);
            LimitQRCode code = limitQRCodeRepository.findByLogin_Id(loginId);
            if (code != null) {
                code.setLastUseTime(LocalDateTime.now());
                limitQRCodeRepository.save(code);
            }
            wechatService.shareTo(loginId, message.getFrom());
        }
    }
}
