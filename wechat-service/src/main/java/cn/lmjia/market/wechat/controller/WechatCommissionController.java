package cn.lmjia.market.wechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 微信端佣金相关控制器
 */
@Controller
public class WechatCommissionController {

    @GetMapping("/wechatCommissionWeekly")
    public String index() {
        return "wechat@commissionWeekly.html";
    }
}
