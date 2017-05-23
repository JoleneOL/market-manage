package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
public class WechatMyController {
    @GetMapping(SystemService.wechatMyURi)
    public String my() {
        return "wechat@personalCenter.html";
    }
}
