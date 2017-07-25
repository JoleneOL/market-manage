package me.jiangcai.logistics.haier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author CJ
 */
@Controller
public class HaierController {

    @PostMapping("/_haier_vom_callback")
    public Object change() {
        return null;
    }

}
