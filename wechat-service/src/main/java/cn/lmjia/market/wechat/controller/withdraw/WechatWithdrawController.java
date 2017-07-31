package cn.lmjia.market.wechat.controller.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.WechatWithdrawService;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import java.math.BigDecimal;

@Controller
public class WechatWithdrawController{

    private static final Log log = LogFactory.getLog(WechatWithdrawController.class);

    @Autowired
    private WechatWithdrawService wechatWithdrawService;

    /**
     * @return 我要提现页面
     */
    @GetMapping("/wechatWithdrawPage")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        return "wechat@withdraw.html";
    }

    /**
     * @return 提现申请提交后，返回申请成功页面
     */
    @PostMapping("/wechatWithdraw")
    public String withdrawNew(@OpenId String openId, HttpServletRequest request, String payee, String account, String bank, String mobile, BigDecimal withdraw,
                              String invoice,String logisticsnumber,String logisticscompany,@AuthenticationPrincipal Login login, Model model)
            throws SystemMaintainException {
        if(login.getCommissionBalance().compareTo(withdraw)<0){
            return "用户可提现余额不足";
        }
        if("0".equals(invoice)) {
            Withdraw withdraw1 = wechatWithdrawService.withdrawNew(payee, account, bank, mobile, withdraw, logisticsnumber, logisticscompany);
        }else if("1".equals(invoice)) {
            Withdraw withdraw1 = wechatWithdrawService.withdrawNew(payee, account, bank, mobile, withdraw, null,null);
        }
        return "wechat@withdrawSuccess.html";
    }

}
