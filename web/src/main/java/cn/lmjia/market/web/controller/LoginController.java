package cn.lmjia.market.web.controller;

import me.jiangcai.wx.web.WeixinEnvironment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 登录相关的控制器
 * 除了在页面上登录还有各种各样的登录方式
 *
 * @author CJ
 */
@Controller
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/toLogin")
    public String toLogin(@WeixinEnvironment boolean weixin) {
        log.debug("[LOGIN] weixin:" + weixin);
        // TODO 如果是微信浏览器 则跳转；支付宝也是如此
        if (weixin)
            return "redirect:/toLoginWechat";
        return "login.html";
    }
}
