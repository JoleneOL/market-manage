package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 代理商管理员可进行
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_PROMOTION + "')")
public class ManagePromotionRequestController {

    @GetMapping("/managePromotionRequest")
    public String index() {
        return "_agentUpdate.html";
    }


}
