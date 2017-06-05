package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
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
