package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SalesmanService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.page.WechatOrderPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 加入关于测试促销人员相关的事宜
 *
 * @author CJ
 */
public class WechatMainOrderControllerTest extends AbstractWechatMainOrderControllerTest {

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private SalesmanService salesmanService;
    @Autowired
    private ReadService readService;

    @Override
    protected WechatOrderPage openOrderPage() {
        driver.get("http://localhost" + SystemService.wechatOrderURi);
        return initPage(WechatOrderPage.class);
    }
    @Test
    public void goWithSalesman() throws Exception {
        // 新增一个用户，然后下单，那么它的order必然与促销无关
        Login login = newRandomLogin();
        bindDeveloperWechat(login);
        updateAllRunWith(login);
        doOrder();
        MainOrder lastOrder = getLastOrder(login);
        assertThat(lastOrder.getSalesAchievement())
                .as("无推荐下时下单，订单不会跟任何销售业绩挂钩")
                .isNull();
        // 然则，发生推荐之后；再进入下单；那么它的order将获得关系；并且该促销人员可以获得佣金
        Login salesmanLogin = newRandomLogin();
        BigDecimal rate = randomRate();
        Salesman salesman = salesmanService.newSalesman(salesmanLogin, rate, null);
        BigDecimal current = readService.currentBalance(salesmanLogin).getAmount();// 之前的余额

        salesmanService.salesmanShareTo(salesman.getId(), login);
        doOrder();
        lastOrder = getLastOrder(login);
        assertThat(lastOrder.getSalesAchievement())
                .as("推荐之后下单应该跟销售业绩挂钩")
                .isNotNull();
        assertThat(lastOrder.getSalesAchievement().getWhose())
                .as("当然业绩不可以给错人了")
                .isEqualTo(salesman);
        makeOrderDone(lastOrder);

        assertThat(readService.currentBalance(salesmanLogin).getAmount())
                .as("该销售应该可以获得一笔佣金，具体多少先不计较了")
                .isGreaterThan(current);
        // 第二次继续下单 则毫无关系！
        doOrder();
        lastOrder = getLastOrder(login);
        assertThat(lastOrder.getSalesAchievement())
                .as("关系不会一直持续的")
                .isNull();

        // 去查看下我的记录
        updateAllRunWith(salesmanLogin);
        driver.get("http://localhost" + SystemService.wechatSales);
        Thread.sleep(2000L);
        System.out.println(driver.getPageSource());
        mockMvc.perform(get("/api/salesList").param("date", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())))
//                .andDo(print())
                .andExpect(status().isOk());
    }

    private BigDecimal randomRate() {
        return new BigDecimal(Math.abs(random.nextDouble()));
    }

    /**
     * @param login 身份
     * @return 这个人刚下过的订单
     */
    private MainOrder getLastOrder(Login login) {
        return mainOrderService.byOrderBy(login).stream()
                .max(Comparator.comparing(MainOrder::getOrderTime)).orElseThrow(()
                        -> new IllegalStateException("没有订单？不可能的"));
    }
}