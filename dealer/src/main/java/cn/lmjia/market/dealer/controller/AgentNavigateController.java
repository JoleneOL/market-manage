package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.mvc.HighestAgent;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 代理商导航相关
 *
 * @author CJ
 */
@Controller
public class AgentNavigateController {

    @Autowired
    private AgentService agentService;

    @RequestMapping(method = RequestMethod.GET, value = "/agentMain")
    public String agentMain(@AuthenticationPrincipal Login login, @HighestAgent AgentLevel agentLevel, Model model) {
        if (login.isManageable()) {
            model.addAttribute("title", "管理后台");
            model.addAttribute("loginAs", login.getLoginTitle());
        } else if (agentLevel != null) {
            model.addAttribute("title", "经销商后台");
            model.addAttribute("loginAs", agentService.loginTitle(agentLevel));
        }

        return "agentMain.html";
    }
}
