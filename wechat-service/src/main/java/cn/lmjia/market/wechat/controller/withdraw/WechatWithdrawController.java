package cn.lmjia.market.wechat.controller.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Controller
public class WechatWithdrawController {

    private static final Log log = LogFactory.getLog(WechatWithdrawController.class);

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ReadService readService;

    /**
     * @return 我要提现页面
     */
    @GetMapping("/wechatWithdraw")
    public String index(Model model) {
        model.addAttribute("rate"
                , NumberFormat.getPercentInstance(Locale.CHINA)
                        .format(withdrawService.getCostRateForNoInvoice().doubleValue()));
        return "wechat@withdraw.html";
    }

    /**
     * @return 提现申请提交后，返回验证码验证页面
     */
    @PostMapping("/wechatWithdraw")
    @Transactional
    public String withdrawNew(String payee, String account
            , String bank, String mobile, BigDecimal withdraw,
                              boolean invoice, String logisticsCode, String logisticsCompany
            , @AuthenticationPrincipal Login login)
            throws SystemMaintainException, IOException {
        log.debug(login.getLoginName() + "申请提现");
        if (readService.currentBalance(login).getAmount().compareTo(withdraw) < 0) {
            return "用户可提现余额不足";
        }
        if (invoice)
            withdrawService.withdrawNew(null, payee, account, bank, mobile, withdraw, logisticsCode
                    , logisticsCompany);
        else
            withdrawService.withdrawNew(null, payee, account, bank, mobile, withdraw, null
                    , null);

        return "wechat@withdrawVerify.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/misc/sendWithdrawCode")
    @ResponseBody
    public ApiResult sendWithdrawCode(@AuthenticationPrincipal Login login) throws IOException {
        verificationCodeService.sendCode(readService.mobileFor(login), withdrawService.withdrawVerificationType());
        return ApiResult.withOk();
    }

    /**
     * @return 手机验证码验证
     */
    @PostMapping("/withdrawVerify")
    public String withdrawVerify(String mobile, String authCode) {
        withdrawService.checkWithdrawCode(mobile, authCode);
        return "wechat@withdrawSuccess.html";
    }
}
