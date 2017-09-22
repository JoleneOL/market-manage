package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.service.*;
import cn.lmjia.market.core.util.LoginAuthentication;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import me.jiangcai.wx.standard.entity.support.AppIdOpenID;
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

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
    private final RequestCache requestCache = new HttpSessionRequestCache();
    @Autowired
    private LoginService loginService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ContactWayService contactWayService;
    @Autowired
    private StandardWeixinUserRepository standardWeixinUserRepository;
    @Autowired
    private PublicAccount publicAccount;
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/wechat/bindTo{id}")
    @Transactional
    @ResponseBody
    public String bindTo(WeixinUserDetail detail, @PathVariable("id") long id) {
//        Login login = loginService.asWechat(openId);
//        if (login != null)
//            return "Failed! You really has bind to Login; please switch your wechatId and try it again.";
        Login login = loginService.get(id);
        if (!(login instanceof Manager)) {
            return "you can only bind wechat to ManagerID";
        }
        if (login.getWechatUser() != null)
            return "there is some one bind th this MangerID!";
        StandardWeixinUser weixinUser = standardWeixinUserRepository.getOne(new AppIdOpenID(publicAccount.getAppID()
                , detail.getOpenId()));
        login.setWechatUser(weixinUser);
        return "success";
    }

    // name mobile  authCode
    @PostMapping("/wechatRegister")
    @Transactional
    public String wechatRegister(@OpenId String openId, String name, String mobile, String authCode) {
        Login login = loginService.asWechat(openId);
        log.trace("微信号已绑定身份：" + login);
        // 用户名是否存在
        final Login mobileLogin = loginService.byLoginName(mobile);
        log.trace("特定手机已绑定身份：" + mobileLogin);
        if (login == null || mobileLogin != null) {
            return "redirect:/wechatRegister";
        }
        verificationCodeService.verify(mobile, authCode, loginService.registerVerificationType());
        // 继续
        login = loginService.password(login, mobile, UUID.randomUUID().toString());

        contactWayService.updateMobile(login, mobile);
        contactWayService.updateName(login, name);

        if (login.getGuideUser() != null)
            noticeService.newLogin(login, mobile);

        return "redirect:" + SystemService.wechatOrderURi;
    }

    @GetMapping("/wechatRegister")
    public String wechatRegister() {
        return "wechat@register.html";
    }

    @GetMapping("/toLoginWechat")
    public String login(WeixinUserDetail detail, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查这个是否具备了 身份，有的话 完成自动登录 否者跳到登录界面
        log.debug(detail);
        Login login = loginService.asWechat(detail.getOpenId());
        if (login == null)
            return "redirect:/wechatLogin";
        // 只有存在用户名才可以登录
        if (StringUtils.isEmpty(login.getUsername())) {
            return "redirect:/wechatRegister";
        }
        // 执行登录

        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = httpSessionSecurityContextRepository.loadContext(holder);

        final LoginAuthentication authentication = new LoginAuthentication(login.getId(), loginService);
        context.setAuthentication(authentication);
//
        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpSessionSecurityContextRepository.saveContext(context, holder.getRequest(), holder.getResponse());
        // 跳转回去！
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            return "redirect:/wechatIndex";
        }

        if (savedRequest.getRedirectUrl().startsWith("http://localhost:-1/")) {
            return "redirect:/" + savedRequest.getRedirectUrl().substring("http://localhost:-1/".length());
        }
//        new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);

        return "redirect:" + savedRequest.getRedirectUrl();
    }

    @GetMapping("/wechatLogout")
    public String logout(@AuthenticationPrincipal Login login) {
        // 登出并且解绑当前登录
        loginService.unbindWechat(login.getLoginName());
        return "redirect:/logout";
    }

    @GetMapping("/wechatIndex")
    public String index() {
        return "wechat@mall/index.html";
//        return "redirect:" + SystemService.wechatMyURi;
    }

    @GetMapping("/wechatSearch")
    public String search(){
        return "wechat@mall/search.html";
    }

    @GetMapping("/wechatLogin")
    public String wechatLogin() {
        return "wechat@login.html";
    }

    @PostMapping("/wechatLogin")
    public String bindLogin(@OpenId String openId, String username, String password, String mobile, String authCode) {
        try {

            if (!StringUtils.isEmpty(username)) {
                // 只有代理商可以登录
                if (loginService.isManager(username))
                    return "redirect:/wechatLogin?type=typeError";
                loginService.bindWechat(username, password, openId);
            } else {
                if (loginService.isManager(mobile))
                    return "redirect:/wechatLogin?type=typeError";
                loginService.bindWechatWithCode(mobile, authCode, openId);
            }

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
