package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
public class ManageController {

    @GetMapping("/manage")
    public String manage() {
        return "_index.html";
    }

    @GetMapping("/orderManage")
    public String orderManage() {
        return "_orderManage.html";
    }

}
