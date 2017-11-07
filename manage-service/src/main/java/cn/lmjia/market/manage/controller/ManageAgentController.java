package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_LOOK + "')")
public class ManageAgentController extends AbstractLoginDetailController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReadService readService;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;

    @PutMapping("/agent/superior/{id}")
    @PreAuthorize("hasAnyRole('ROOT')")
    @ResponseBody
    @Transactional
    public ApiResult changeSuperior(@RequestBody String newGuide, @PathVariable("id") long id) {
        final AgentLevel target = agentService.getAgent(id);

        if (!StringUtils.isEmpty(newGuide)) {
            long guideId = NumberUtils.parseNumber(newGuide, Long.class);
            AgentLevel targetLevel = agentService.getAgent(loginService.get(guideId), target.getLevel() - 1);
            loginRelationCacheService.breakConnection(target);
            target.setSuperior(targetLevel);
            loginRelationCacheService.addLowestAgentLevelCache(target.getSuperior());
            return ApiResult.withOk(Collections.singletonMap("name", readService.nameForAgent(targetLevel)));
        }
        return ApiResult.withCodeAndMessage(400, "没有有效的上级代理商", null);
    }

    @PutMapping("/agent/rank/{id}")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_MANAGER + "')")
    @ResponseBody
    @Transactional
    public ApiResult changeRank(@RequestBody String newName, @PathVariable("id") long id) {
        if (!StringUtils.isEmpty(newName)) {
            agentService.getAgent(id).setRank(newName);
            return ApiResult.withOk();
        }
        return ApiResult.withCodeAndMessage(400, "没有有效的名称", null);
    }

    @GetMapping("/manageAgentDetail")
    @Transactional(readOnly = true)
    public String detail(Model model, long id) {
        final AgentLevel agent = agentService.getAgent(id);
        model.addAttribute("currentData", agent);
        return _detailView(model, agent.getLogin());
    }

    @Override
    public String detailTitle() {
        return "代理商详情";
    }

    @Override
    public String parentUri() {
        return "/agentManage";
    }

    @Override
    public String parentTitle() {
        return "代理商管理";
    }
}
