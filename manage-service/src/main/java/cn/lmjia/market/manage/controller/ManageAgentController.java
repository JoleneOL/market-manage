package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_LOOK + "')")
public class ManageAgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping("/manageAgentDetail")
    @Transactional(readOnly = true)
    public String detail(Model model, long id) {
        final AgentLevel agent = agentService.getAgent(id);
        model.addAttribute("currentData", agent);
        model.addAttribute("login", agent.getLogin());
        return "_agentDetail.html";
    }

}
