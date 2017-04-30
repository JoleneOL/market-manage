package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.mvc.HighestAgent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
public class WelcomeController {

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String index(@AuthenticationPrincipal Login login, @HighestAgent AgentLevel agentLevel) {
        // 如果当前登录者是管理员 或者是代理体系内的一般代理则都给予引导至管理页
        if (login.isManageable() || agentLevel != null)
            return "main.html";
        throw new IllegalStateException("不知道引到至何处。");
    }

}
