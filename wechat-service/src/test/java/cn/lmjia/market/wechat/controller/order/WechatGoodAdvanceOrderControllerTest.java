package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.AgentFinancingService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import cn.lmjia.market.wechat.page.WechatGoodAdvanceOrderPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 测试预付货款下单
 *
 * @author CJ
 */
public class WechatGoodAdvanceOrderControllerTest extends WechatTestBase {

    @Autowired
    private AgentFinancingService agentFinancingService;

    @Test
    public void go() {
        // 一个崭新的代理商 自然会因为余额不足 无法下单
        Login login = newRandomAgent();
        updateAllRunWith(login);

        WechatGoodAdvanceOrderPage page = WechatGoodAdvanceOrderPage.of(this, driver);

        page.submitRandomOrder(null, null);
        page.assertHaveTooltip();

        // 给它添加足够多的货款
        AgentGoodAdvancePayment payment = agentFinancingService.addGoodPayment(newRandomManager(), login.getId()
                , new BigDecimal("9999999"), LocalDate.now(), null);

        page = WechatGoodAdvanceOrderPage.of(this, driver);

        page.submitRandomOrder(null, null);
        page.assertHaveTooltip();

        agentFinancingService.approvalGoodPayment(newRandomManager(ManageLevel.finance), payment.getId(), "ok");

        Manager manager = newRandomManager(ManageLevel.customerService);
        bindDeveloperWechat(manager);

        page = WechatGoodAdvanceOrderPage.of(this, driver);

        page.submitRandomOrder(null, null);
        PaySuccessPage.waitingForSuccess(this, driver, 2, null);
    }

}