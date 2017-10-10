package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.service.CommissionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author lxf
 */
@Controller
public class CommissionDetailsController {

    @Autowired
    private CommissionDetailService commissionDetailsService;

    @RequestMapping("/orderDetail/Commission")
    public String commissionDetail(String orderId, Model model) {
        long oId = Long.parseLong(orderId);
        //根据订单id查询佣金详情
        List<Commission> result = commissionDetailsService.findByOrderId(oId);
        if (result != null) {
            model.addAttribute("commissionDetail", result);
            return "_commissionDetail";
        }
        return "_commissionDetail";
    }

    ;
}
