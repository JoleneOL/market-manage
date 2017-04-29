package cn.lmjia.market.web.controller;

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

    @RequestMapping(method = RequestMethod.GET, value = "/toLogin")
    public String toLogin() {
        // TODO 如果是微信浏览器 则跳转；支付宝也是如此
        return "login.html";
    }
}
