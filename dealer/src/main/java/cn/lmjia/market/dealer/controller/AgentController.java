package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping("/agentManage")
    public String index() {
        return "agentManage.html";
    }

    @GetMapping("/agentDetail")
    public String detail(long id, Model model) {
        // TODO 有几个条件准入，1 管理员  2 我可以管辖这个agent
        model.addAttribute("agent", agentService.getAgent(id));
        return "agentDetail.html";
    }
}
