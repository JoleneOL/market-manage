package cn.lmjia.market.core.controller.index;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.wx.web.WeixinEnvironment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录相关的控制器
 * 除了在页面上登录还有各种各样的登录方式
 *
 * @author CJ
 */
@Controller
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);
    @Autowired
    private LoginService loginService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(method = RequestMethod.GET, value = "/toLogin")
    public String toLogin(@WeixinEnvironment boolean weixin) {
        log.debug("[LOGIN] weixin:" + weixin);
        // TODO 如果是微信浏览器 则跳转；支付宝也是如此
        if (weixin)
            return "redirect:/toLoginWechat";
        return "login.html";
    }

    @GetMapping("/loginChangePassword")
    public String indexForLoginChangePassword() {
        return "_changePassword.html";
    }

    @PostMapping("/loginChangePassword")
    @ResponseBody
    @Transactional
    public String loginChangePassword(@AuthenticationPrincipal Login login, String originPassword, String newPassword
            , String newPassword2) {
        Login newLogin = loginService.get(login.getId());
        if (!passwordEncoder.matches(originPassword, newLogin.getPassword())) {
            return "bad originPassword";
        }
        if (!newPassword.equals(newPassword2))
            return "bad newPassword";
        loginService.password(newLogin, newPassword);
        return "success";
    }
}
