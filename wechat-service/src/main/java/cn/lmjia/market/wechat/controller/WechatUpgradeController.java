package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.PayAssistanceService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.request.PromotionRequestService;
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
import java.io.IOException;

/**
 * @author CJ
 */
@Controller
public class WechatUpgradeController {

    @Autowired
    private PromotionRequestService promotionRequestService;
    @Autowired
    private PayAssistanceService payAssistanceService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReadService readService;

    @GetMapping("/wechatUpgrade")
    @Transactional(readOnly = true)
    public String index(@AuthenticationPrincipal Login loginInput, Model model) {
        // 正在申请 错误
        Login login = loginService.get(loginInput.getId());
        if (!loginService.isRegularLogin(login)) {
            return "redirect:" + SystemService.wechatOrderURi;
        }
        if (promotionRequestService.currentRequest(login) != null) {
//            throw new IllegalStateException("申请正在处理中。");
            return "redirect:/wechatUpgradeChecking";
        }

        model.addAttribute("price", new Money(promotionRequestService.getPriceFor1()));
        int level = readService.agentLevelForPrincipal(login);
        boolean u1Enable = level > 4;
        boolean u2Enable = level > 3;
        boolean u3Enable = level > 2;

        model.addAttribute("u1Enable", u1Enable);
        model.addAttribute("u2Enable", u2Enable);
        model.addAttribute("u3Enable", u3Enable);

        return "wechat@update.html";
    }

    @GetMapping("/wechatUpgradeChecking")
    public String checking() {
        return "wechat@waitFor.html";
    }

    @GetMapping("/wechatUpgradeApplySuccess")
    public String applySuccess() {
        return "wechat@updateSuccess.html";
    }

    @PostMapping("/wechatUpgrade")
    @Transactional
    public ModelAndView upgrade(@AuthenticationPrincipal Login login, String agentName, int newLevel, Address address
            , String cardFrontPath
            , String cardBackPath, String businessLicensePath, String upgradeMode, HttpServletRequest servletRequest) throws SystemMaintainException, IOException {
        // 申请之后。如果是区代理则进入申请成功界面；反之则进入支付界面（允许选择使用支付方式）
        PromotionRequest request = promotionRequestService.initRequest(login, agentName, newLevel, address, cardBackPath
                , cardFrontPath, businessLicensePath);

        // 如果是申请省代理 则现在就结束
        if (request.getOrderDueAmount() == null) {
            promotionRequestService.submitRequest(request);
            return new ModelAndView("redirect:/wechatUpgradeApplySuccess");
        }
        // upgradeMode 如果是1 就付费 如果是2 则直接提交
        if ("2".equals(upgradeMode)) {
            promotionRequestService.submitRequest(request);
            return new ModelAndView("redirect:/wechatUpgradeApplySuccess");
        }

        // 开始支付
        return payAssistanceService.payOrder(login.getWechatUser().getOpenId(), servletRequest, request);
    }

}
