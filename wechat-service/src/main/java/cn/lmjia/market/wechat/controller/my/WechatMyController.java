package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;

/**
 * @author CJ
 */
@Controller
public class WechatMyController {

    @Autowired
    private TeamService teamService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @GetMapping(SystemService.wechatMyURi)
    public String my() {
        return "wechat@personalCenter.html";
    }

    @GetMapping(SystemService.wechatMyTeamURi)
    @Transactional(readOnly = true)
    public String myTeam(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("allCount", teamService.all(login));
        return "wechat@myTeam.html";
    }
}
