package cn.lmjia.market.core.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class TestController {

    @GetMapping("/redirectSuccessUri")
    public String redirectSuccessUri(String successUri) {
        return "redirect:" + successUri;
    }

}
