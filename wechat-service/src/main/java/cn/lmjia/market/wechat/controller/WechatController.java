package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.wx.OpenId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LoginService loginService;

    @GetMapping("/toLoginWechat")
    public String login(@OpenId String openId) {
        // 检查这个是否具备了 身份，有的话 完成自动登录 否者跳到登录界面
        log.debug(openId);
        Login login = loginService.asWechat(openId);
        if (login == null)
            return "redirect:/wechatLogin";
        return "login.html";
    }

    @GetMapping("/wechatLogin")
    public String wechatLogin() {
        return "wechat@login.html";
    }
}
