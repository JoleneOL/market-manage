package cn.lmjia.market.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
public class WelcomeController {

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String index() {
        return "help";
    }

}
