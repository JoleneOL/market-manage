package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.util.LoginAuthentication;
import com.huotu.verification.IllegalVerificationCodeException;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 几个微信常用控制器
 *
 * @author CJ
 */
@Controller
public class WechatController {

    private static final Log log = LogFactory.getLog(WechatController.class);
    private final SecurityContextRepository httpSessionSecurityContextRepository
            = new HttpSessionSecurityContextRepository();
    @Autowired
    private LoginService loginService;

    @GetMapping("/toLoginWechat")
    public String login(WeixinUserDetail detail, HttpServletRequest request, HttpServletResponse response) {
        // 检查这个是否具备了 身份，有的话 完成自动登录 否者跳到登录界面
        log.debug(detail);
        Login login = loginService.asWechat(detail.getOpenId());
        if (login == null)
            return "redirect:/wechatLogin";
        // 执行登录

        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = httpSessionSecurityContextRepository.loadContext(holder);

        final LoginAuthentication authentication = new LoginAuthentication(login.getId(), loginService);
        context.setAuthentication(authentication);
//
        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpSessionSecurityContextRepository.saveContext(context, holder.getRequest(), holder.getResponse());

        return "redirect:/wechatIndex";
    }

    @GetMapping("/wechatIndex")
    public String index() {
        return "wechat@index.html";
    }

    @GetMapping("/wechatLogin")
    public String wechatLogin() {
        return "wechat@login.html";
    }

    @PostMapping("/wechatLogin")
    public String bindLogin(@OpenId String openId, String username, String password, String mobile, String authCode) {
        try {
            if (!StringUtils.isEmpty(username))
                loginService.bindWechat(username, password, openId);
            else
                loginService.bindWechatWithCode(mobile, authCode, openId);
        } catch (IllegalArgumentException ex) {
            log.debug("", ex);
            if (!StringUtils.isEmpty(username))
                return "redirect:/wechatLogin?type=error";
            return "redirect:/wechatLogin?type=codeError";
        } catch (IllegalVerificationCodeException ex) {
            return "redirect:/wechatLogin?type=codeError";
        }
        return "redirect:/toLoginWechat";
    }
}
