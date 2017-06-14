package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import me.jiangcai.payment.paymax.PaymaxPaymentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
public class ManageController {

    @Autowired
    private PaymaxPaymentForm paymaxPaymentForm;

    @GetMapping("/manage")
    public String manage() {
        return "_index.html";
    }

    @GetMapping("/orderManage")
    public String orderManage() {
        return "_orderManage.html";
    }

    @PutMapping("/order/orderMaintain")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void orderMaintain() {
        paymaxPaymentForm.orderMaintain();
    }

}
