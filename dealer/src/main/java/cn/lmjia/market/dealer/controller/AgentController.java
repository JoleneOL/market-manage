package cn.lmjia.market.dealer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class AgentController {
    @GetMapping("/agentManage")
    public String index() {
        return "agentManage.html";
    }
}
