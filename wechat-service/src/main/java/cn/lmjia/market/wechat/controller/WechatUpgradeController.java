package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Controller
public class WechatUpgradeController {

    @Autowired
    private PromotionRequestService promotionRequestService;
    @Autowired
    private SystemStringService systemStringService;

    @GetMapping("/wechatUpgrade")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        // 正在申请 错误
        if (promotionRequestService.currentRequest(login) != null) {
            throw new IllegalStateException("申请正在处理中。");
        }

        model.addAttribute("price", new Money(systemStringService.getCustomSystemString("market.price.promotion.agent"
                , null, true, BigDecimal.class, new BigDecimal("30000"))));

        return "wechat@update.html";
    }

    @PostMapping("/wechatUpgrade")
    public String upgrade(@AuthenticationPrincipal Login login, int newLevel, String address, String cardFrontPath
            , String cardBackPath, String businessLicensePath, String offlinePayComment) {
        // 申请之后。如果是区代理则进入申请成功界面；反之则进入支付界面（允许选择使用支付方式）
        return null;
    }

}
