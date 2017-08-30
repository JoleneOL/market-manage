package cn.lmjia.market.wechat.controller.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest_;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.repository.WithdrawRequestRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private WithdrawRequestRepository withdrawRequestRepository;
    @Autowired
    private ReadService readService;
    @Autowired
    private SystemStringService systemStringService;

    @GetMapping("/wechatWithdrawRecord")
    public String record() {
        return "wechat@withdrawRecord.html";
    }

    @GetMapping("/wechatWithdrawRecordData")
    @Transactional(readOnly = true)
    public String recordData(int page, @AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("dataList", withdrawRequestRepository.findAll((root, query, cb)
                -> cb.and(
                cb.equal(root.get(WithdrawRequest_.whose), login)
                , root.get(WithdrawRequest_.withdrawStatus)
                        .in(WithdrawStatus.checkPending, WithdrawStatus.refuse, WithdrawStatus.success)
        ), new PageRequest(page, 5, new Sort(Sort.Direction.DESC, WithdrawRequest_.requestTime.getName()))));
        return "wechat@withdrawRecordData.html";
    }

    /**
     * @return 我要提现页面
     */
    @GetMapping("/wechatWithdraw")
    public String index(Model model) {
        model.addAttribute("rate"
                , NumberFormat.getPercentInstance(Locale.CHINA)
                        .format(withdrawService.getCostRateForNoInvoice().doubleValue()));
        model.addAttribute("companyName", systemStringService.getCustomSystemString("withdraw.invoice.companyName"
                , null, true, String.class, "利每家科技有限公司"));
        model.addAttribute("companyAddress", systemStringService.getCustomSystemString("withdraw.invoice.companyAddress"
                , null, true, String.class, "杭州市滨江区滨盛路1508号海亮大厦1803室"));
        model.addAttribute("companyTelephone", systemStringService.getCustomSystemString("withdraw.invoice.companyTelephone"
                , null, true, String.class, "0570-88187913"));
        model.addAttribute("taxpayerCode", systemStringService.getCustomSystemString("withdraw.invoice.taxpayerCode"
                , null, true, String.class, "91330108MA28MBU173"));
        model.addAttribute("bankName", systemStringService.getCustomSystemString("withdraw.invoice.bankName"
                , null, true, String.class, "兴业银行杭州滨江支行"));
        model.addAttribute("bankAccount", systemStringService.getCustomSystemString("withdraw.invoice.bankAccount"
                , null, true, String.class, "356940100100162419"));
        model.addAttribute("content", systemStringService.getCustomSystemString("withdraw.invoice.content"
                , null, true, String.class, "服务费或劳务费的增值发票"));

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
            , @AuthenticationPrincipal Login login, Model model)
            throws SystemMaintainException, IOException {
        log.debug(login.getLoginName() + "申请提现");
        if (readService.currentBalance(login).getAmount().compareTo(withdraw) < 0) {
            return "redirect:/wechatWithdraw";
        }
        if (invoice)
            withdrawService.withdrawNew(login, payee, account, bank, mobile, withdraw, logisticsCode
                    , logisticsCompany);
        else
            withdrawService.withdrawNew(login, payee, account, bank, mobile, withdraw, null
                    , null);
        model.addAttribute("badCode", false);
        return toVerify(login, model);
    }

    private String toVerify(Login login, Model model) {
        String mobile = readService.mobileFor(login);
        String start = mobile.substring(0, 3);
        String end = mobile.substring(mobile.length() - 4, mobile.length());
        model.addAttribute("mosaicMobile", start + "****" + end);
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
    public String withdrawVerify(@AuthenticationPrincipal Login login, String authCode, Model model) {
        try {
            withdrawService.submitRequest(login, authCode);
        } catch (IllegalVerificationCodeException ex) {
            model.addAttribute("badCode", true);
            return toVerify(login, model);
        }
        return "wechat@withdrawSuccess.html";
    }
}
