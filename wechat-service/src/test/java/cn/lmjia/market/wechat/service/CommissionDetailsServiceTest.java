package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.service.CommissionDetailService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.CommissionWeeklyPage;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class CommissionDetailsServiceTest extends WechatTestBase{
    @Autowired
    private CommissionDetailService commissionDetailService;
    @Autowired
    private ReadService readService;

    @Test
    public void go() {
        //创建一个用户下单
        Login login = randomLogin(false);
        updateAllRunWith(login);

        bindDeveloperWechat(login);
        //下单
        MainOrder mainOrder = newRandomOrderFor(login, login);
        makeOrderPay(mainOrder);

        //根据订单查询详情
        System.out.println("登录用户"+login.getLoginName());
        List<Commission> result = commissionDetailService.findByOrderId(mainOrder.getId());
        BigDecimal totalAmount = new BigDecimal(0);
        for (Commission commission : result) {
            totalAmount = totalAmount.add(commission.getAmount());
            System.out.println("佣金获取人:" + readService.nameForPrincipal(commission.getWho()) + "分佣金额:" + commission.getAmount() + "分佣比例:" + commission.getRate()+"分佣类型:"+commission.getType());
        }

        commissionDetailService.sendComissionDetailWeekly();

        driver.get("http://localhost/wechatCommissionWeekly");
        CommissionWeeklyPage commissionWeeklyPage = initPage(CommissionWeeklyPage.class);
        //打印一下
        commissionWeeklyPage.printPage();

        //commissionWeeklyPage.assertAmount(totalAmount);
    }
}
