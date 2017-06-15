package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author CJ
 */
@Controller
public class ManageRoleController {

    @Autowired
    private LoginService loginService;

    // 添加一个管理员
    @PostMapping("/managers/add")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_GRANT + "')")
    @Transactional
    public String add(@AuthenticationPrincipal Manager login, String loginName, String rawPassword, ManageLevel level) {
        // root 可以添加任意角色，但是经理是有限定的
        if (login.getLevel() != ManageLevel.root && (
                level == ManageLevel.manager
                        || level == ManageLevel.root
        ))
            throw new AccessDeniedException("被阻止的越权行为。");

        Manager manager = loginService.newLogin(Manager.class, loginName, login, rawPassword);
        manager.setLevel(level);

        return "redirect:/managers";
    }

}
