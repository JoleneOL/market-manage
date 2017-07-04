package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.converter.QRController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class WechatMyController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private QRController qrController;

    // 二维码扫码将是一个永久场景(欢迎加入)二维码
    // 扫码需要关注，并且在关注后自动引导事件
    // S1.(更新这个永久场景的最后使用时间)
    // (确保这个微信并没有被关联在现有用户系统中，自动创建匿名用户，并且与之绑定)
    // (基于测试的便利，这里没有加入这个微信号也必须是新的判定)
    //
    // 链接则是 /wechatJoin?id=x(二维码ID)
    // 如果检测到未关注，则弹出页面展示该二维码
    // 如果已关注，go S1,然后引导至下单页
    //
    // 所以我们得先解决 永久二维码问题
    @GetMapping(SystemService.wechatPromotionUri)
    public String promotion(@AuthenticationPrincipal Login login, Model model) {
        if (!loginService.isRegularLogin(login))
            return "redirect:" + SystemService.wechatPromotionMoreUri;
        model.addAttribute("login", login);


        return "wechat@promotion.html";
    }

    @GetMapping("/wechatOrderList")
    public String wechatOrderList() {
        return "wechat@orderList.html";
    }

    @GetMapping(SystemService.wechatMyURi)
    public String my() {
        return "wechat@personalCenter.html";
    }

    @GetMapping(SystemService.wechatMyTeamURi)
    @Transactional(readOnly = true)
    public String myTeam(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("agentLevel", agentService.loginTitle(agentService.highestAgent(login)));
        // 微信头像 名字 等级
        model.addAttribute("allCount", teamService.all(login));

        int lowestLevel = systemService.systemLevel() - 1;
        model.addAttribute("count0", teamService.agents(login, lowestLevel - 2));
        model.addAttribute("count1", teamService.agents(login, lowestLevel - 1));
        model.addAttribute("count2", teamService.agents(login, lowestLevel));
        model.addAttribute("count3", teamService.validCustomers(login));
        return "wechat@myTeam.html";
    }
}
