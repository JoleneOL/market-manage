package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.data_table.DataPageable;
import cn.lmjia.market.core.data_table.DrawablePageAndSelection;
import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 可以调整代理的控制器
 *
 * @author CJ
 */
@Controller
@RequestMapping("/agentData")
public class AgentDataController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private ReadService readService;

    @GetMapping(value = "/list")
    public DrawablePageAndSelection<AgentLevel> list(@AuthenticationPrincipal Login login, String agentName, DataPageable pageable) {
        Page<AgentLevel> agentLevelPage = agentService.manageable(login, agentName, pageable);
        return new DrawablePageAndSelection<>(pageable, agentLevelPage, AgentLevel.ManageSelections(readService));
    }

}
