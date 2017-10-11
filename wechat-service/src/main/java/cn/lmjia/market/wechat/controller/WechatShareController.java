package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.SalesmanService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.service.WechatService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import me.jiangcai.wx.protocol.exception.BadAccessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author CJ
 */
@Controller
public class WechatShareController {

    private static final Log log = LogFactory.getLog(WechatShareController.class);
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
    @Autowired
    private QRController qrController;
    @Autowired
    private SalesmanService salesmanService;

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
        if (!loginService.isRegularLogin(login) && !loginService.allowShare(login))
            return "redirect:" + SystemService.wechatShareMoreUri;
        model.addAttribute("login", login);
        final String targetUrl = systemService.toUrl("/wechatJoin" + login.getId());
        model.addAttribute("qrCodeUrl", qrController.urlForText(targetUrl).toString());
        model.addAttribute("url", targetUrl);
        return "wechat@shareQC.html";
    }

    @GetMapping(SystemService.wechatShareMoreUri)
    public String shareMore(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("regular", loginService.isRegularLogin(login));
        return "wechat@chance.html";
    }

    /**
     * 销售人员推广的地址
     *
     * @param id     销售人员id
     * @param openId 用户openId
     * @param login  当前身份
     * @param model  model
     * @return
     */
    @GetMapping("/wechatJoinSM{id}")
    @Transactional
    public String joinBySalesman(@PathVariable long id, @OpenId String openId, @AuthenticationPrincipal Object login
            , Model model) {
        Salesman salesman = salesmanService.get(id);
        // 如果已登录 那么直接去下单
        if (login != null && login instanceof Login) {
            salesmanService.salesmanShareTo(id, (Login) login);
            return "redirect:" + SystemService.wechatOrderURi;
        }

        // 看看是否已关注
        //  必须关注本公众号才可以 测试环境可以跳过
        final Protocol protocol = Protocol.forAccount(publicAccount);
        try {
            if (!environment.acceptsProfiles("unit_test")
                    && !protocol.userDetail(openId).isSubscribe()) {
                model.addAttribute("qrCodeUrl", wechatService.qrCodeFor(salesman));
                return "wechat@subscribe_required.html";
            }
        } catch (BadAccessException ex) {
            // 检测是否关注失败
            log.debug("检测是否关注失败", ex);
        }

        salesmanService.salesmanShareTo(id, wechatService.shareTo(id, openId));
        return "redirect:" + SystemService.wechatOrderURi;
    }

    /**
     * 用户打开了其他人分享的推广地址
     *
     * @param id     谁分享的
     * @param openId 用户openId
     * @param login  当前身份
     * @param model  model
     * @return
     */
    @GetMapping("/wechatJoin{id}")
    public String join(@PathVariable long id, @OpenId String openId, @AuthenticationPrincipal Object login, Model model) {
        // 如果已登录 那么直接去下单
        if (login != null && login instanceof Login)
            return "redirect:" + SystemService.wechatOrderURi;
        // 看看是否已关注
        //  必须关注本公众号才可以 测试环境可以跳过
        final Protocol protocol = Protocol.forAccount(publicAccount);
        try {
            if (!environment.acceptsProfiles("unit_test")
                    && !protocol.userDetail(openId).isSubscribe()) {
                model.addAttribute("qrCodeUrl", wechatService.qrCodeForLogin(loginService.get(id)));
                return "wechat@subscribe_required.html";
            }
        } catch (BadAccessException ex) {
            // 检测是否关注失败
            log.debug("检测是否关注失败", ex);
        }

        wechatService.shareTo(id, openId);
        return "redirect:" + SystemService.wechatOrderURi;
    }
}
