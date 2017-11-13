package cn.lmjia.market.manage.controller;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.service.CommissionDetailService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 测试佣金详情分布
 *
 * @author lxf
 */
@Ignore
public class CommissionDetailsControllerTest extends ManageServiceTest {

    @Autowired
    private CommissionDetailService commissionDetailService;
    @Autowired
    private ReadService readService;

    @Test
    public void go() {
        //创建一个用户下单
        Login login = randomLogin(false);
        updateAllRunWith(login);

        //下单
        MainOrder mainOrder = newRandomOrderFor(login, login);
        makeOrderPay(mainOrder);

        //根据订单查询详情
        List<Commission> result = commissionDetailService.findByOrderId(mainOrder.getId());
        for (Commission commission : result) {
            System.out.println("佣金获取人:" + readService.nameForPrincipal(commission.getWho()) + "分佣金额:" + commission.getAmount() + "分佣比例:" + commission.getRate()+"分佣类型:"+commission.getType());
        }

    }
}
