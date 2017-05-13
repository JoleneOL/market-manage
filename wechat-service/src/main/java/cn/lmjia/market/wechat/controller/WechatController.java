package cn.lmjia.market.wechat.controller;

import me.jiangcai.wx.model.WeixinUserDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 几个微信常用控制器
 *
 * @author CJ
 */
@Controller
public class WechatController {

    private static final Log log = LogFactory.getLog(WechatController.class);

    @GetMapping("/toLoginWechat")
    public String login(WeixinUserDetail detail) {
        // 检查这个是否具备了 身份，有的话 完成自动登录 否者跳到登录界面
        log.debug(detail.getNickname());
        return "login.html";
    }
}
