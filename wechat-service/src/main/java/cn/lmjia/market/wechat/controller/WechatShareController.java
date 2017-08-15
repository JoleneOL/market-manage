package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.service.WechatService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class WechatShareController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private PublicAccount publicAccount;
    @Autowired
    private Environment environment;

    // 二维码扫码将是一个永久场景(欢迎加入)二维码
    // 扫码需要关注，并且在关注后自动引导事件
    // S1.(更新这个永久场景的最后使用时间)
    // (确保这个微信并没有被关联在现有用户系统中，自动创建匿名用户，并且与之绑定)
    // (基于测试的便利，这里没有加入这个微信号也必须是新的判定)
    //
    // 链接则是 /wechatJoin?id=x(用户Id)
    // 如果检测到未关注，则弹出页面展示该二维码
    // 如果已关注，go S1,然后引导至下单页
    //
    // 所以我们得先解决 永久二维码问题
    @GetMapping(SystemService.wechatShareUri)
    public String share(@AuthenticationPrincipal Login loginInput, Model model) {
        Login login = loginService.get(loginInput.getId());
        if (!loginService.isRegularLogin(login))
            return "redirect:" + SystemService.wechatShareMoreUri;
        model.addAttribute("login", login);
        model.addAttribute("qrCodeUrl", wechatService.qrCodeForLogin(login).getImageUrl());
        model.addAttribute("url", systemService.toUrl("/wechatJoin?id=" + login.getId()));
        return "wechat@shareQC.html";
    }

    @GetMapping(SystemService.wechatShareMoreUri)
    public String shareMore(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("regular", loginService.isRegularLogin(login));
        return "wechat@chance.html";
    }

    @GetMapping("/wechatJoin")
    public String join(long id, @OpenId String openId, @AuthenticationPrincipal Object login, Model model) {
        // 如果已登录 那么直接去下单
        if (login != null && login instanceof Login)
            return "redirect:" + SystemService.wechatOrderURi;
        // 看看是否已关注
        //  必须关注本公众号才可以 测试环境可以跳过
        final Protocol protocol = Protocol.forAccount(publicAccount);
        if (!environment.acceptsProfiles("unit_test")
                && !protocol.userDetail(openId).isSubscribe()) {
            model.addAttribute("qrCodeUrl", wechatService.qrCodeForLogin(loginService.get(id)));
            return "wechat@subscribe_required.html";
        }

        wechatService.shareTo(id, openId);
        return "redirect:" + SystemService.wechatOrderURi;
    }
}
