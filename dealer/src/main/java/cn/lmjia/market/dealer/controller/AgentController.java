package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author CJ
 */
@Controller
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ContactWayService contactWayService;

    @GetMapping("/agentManage")
    public String index(@AuthenticationPrincipal Login login) {
        if (login.isManageable())
            return "_agentManage.html";
        return "agentManage.html";
    }

    @GetMapping("/agentDetail")
    public String detail(long id, Model model) {
        // TODO 有几个条件准入，1 管理员  2 我可以管辖这个agent
        model.addAttribute("agent", agentService.getAgent(id));
        return "agentDetail.html";
    }

    // 目前仅允许管理员 添加
    // 实际上应该是允许更多人干这个事儿
    @GetMapping("/addAgent")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "')")
    public String indexForAdd() {
        return "addAgent.html";
    }

    @PostMapping("/addAgent")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "')")
    @Transactional
    public String addAgent(@AuthenticationPrincipal Login login, Long superiorId, String rank, String agentName
            , int firstPayment, int agencyFee, @RequestParam LocalDate beginDate, @RequestParam LocalDate endDate
            , String mobile, String password
            , long guideUser, Address address, String cardFrontPath, String cardBackPath) throws IOException {
        Login guide = loginService.get(guideUser);

        Login newLogin = loginService.newLogin(mobile, guide, password);
        contactWayService.updateName(newLogin, agentName);
        contactWayService.updateMobile(newLogin, mobile);
        contactWayService.updateAddress(newLogin, address);
        contactWayService.updateIDCardImages(newLogin, cardFrontPath, cardBackPath);

        agentService.addAgent(login, newLogin, rank, beginDate, endDate, firstPayment, agencyFee
                , superiorId == null ? null : agentService.getAgent(superiorId));

        return "redirect:/agentManage";
    }
}
