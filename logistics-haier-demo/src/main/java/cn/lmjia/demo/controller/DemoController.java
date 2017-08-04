package cn.lmjia.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author CJ
 */
@Controller
public class DemoController {

    @GetMapping({"", "/"})
    public RedirectView index() {
        return new RedirectView("/index.html", true);
    }

}
