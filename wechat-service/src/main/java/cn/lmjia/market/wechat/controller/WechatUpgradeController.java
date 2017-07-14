package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.service.PayAssistanceService;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private PayAssistanceService payAssistanceService;

    @GetMapping("/wechatUpgrade")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        // 正在申请 错误
        if (promotionRequestService.currentRequest(login) != null) {
//            throw new IllegalStateException("申请正在处理中。");
            return "redirect:/wechatUpgradeChecking";
        }

        model.addAttribute("price", new Money(systemStringService.getCustomSystemString("market.price.promotion.agent"
                , null, true, BigDecimal.class, new BigDecimal("30000"))));

        return "wechat@update.html";
    }

    @GetMapping("/wechatUpgradeChecking")
    public String checking() {
        return "wechat@updateSuccess.html";
    }

    @PostMapping("/wechatUpgrade")
    @Transactional
    public ModelAndView upgrade(@AuthenticationPrincipal Login login, int newLevel, String address, String cardFrontPath
            , String cardBackPath, String businessLicensePath, String offlinePayComment, HttpServletRequest servletRequest) throws SystemMaintainException {
        // 申请之后。如果是区代理则进入申请成功界面；反之则进入支付界面（允许选择使用支付方式）
        PromotionRequest request = promotionRequestService.initRequest(login, newLevel, address, cardBackPath
                , cardFrontPath, businessLicensePath);

        // 如果是申请省代理 则现在就结束
        if (request.getOrderDueAmount() == null) {
            promotionRequestService.submitRequest(request);
            return new ModelAndView("redirect:/wechatUpgradeChecking");
        }

        // 开始支付
        return payAssistanceService.payOrder(login.getWechatUser().getOpenId(), servletRequest, request);
    }

}
