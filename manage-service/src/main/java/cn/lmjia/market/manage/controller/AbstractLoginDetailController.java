package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import org.springframework.ui.Model;

/**
 * @author CJ
 */
public abstract class AbstractLoginDetailController {

    /**
     * @param model model
     * @param login 身份
     * @return 可以获取详情页面的视图名称
     */
    String _detailView(Model model, Login login) {
        model.addAttribute("login", login);
        model.addAttribute("parentTitle", parentTitle());
        model.addAttribute("parentUri", parentUri());
        model.addAttribute("detailTitle", detailTitle());
        return "_agentDetail.html";
    }

    abstract String detailTitle();

    abstract String parentUri();

    abstract String parentTitle();

}
