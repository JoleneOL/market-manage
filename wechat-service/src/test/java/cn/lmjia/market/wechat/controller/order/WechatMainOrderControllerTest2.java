package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import cn.lmjia.market.core.trj.TRJService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import cn.lmjia.market.wechat.page.WechatOrderPage;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 针对投融家的订单测试
 *
 * @author CJ
 */
@ContextConfiguration(classes = SecurityConfig.class)
public class WechatMainOrderControllerTest2 extends WechatTestBase {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private TRJService trjService;

    @Test
    public void go() throws Exception {
        //选择一个商品的价格 认定它为投融家价格
        BigDecimal price = mainGoodService.forSale().stream().max(new RandomComparator()).orElse(null).getTotalPrice();
        systemStringService.updateSystemString(TRJEnhanceConfig.SS_PriceKey, price);

        updateAllRunWith(randomLogin(false));
        mockMvc.perform(wechatGet(TRJEnhanceConfig.TRJOrderURI))
                .andExpect(status().isOk())
                .andExpect(view().name("wechat@orderPlace.html"));
        // 应该只能看到部分商品
        driver.get("http://localhost" + TRJEnhanceConfig.TRJOrderURI);
        WechatOrderPage orderPage = initPage(WechatOrderPage.class);

        orderPage.allPriced(price);

        // 特定按揭码和身份证给他们
        OrderRequest request = randomOrderRequest(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomNumeric(18));
        String result = submitOrderRequest(request);
        // 应该是一个错误地址
        assertThat(result)
                .contains("InvalidAuthorisingException");
        // 新增按揭码
        final String authorising = RandomStringUtils.randomAlphabetic(10);
        final String idNumber = RandomStringUtils.randomNumeric(18);
        addAuthorising(authorising, idNumber);
        // 使用刚新增的按揭码
        request = randomOrderRequest(authorising, idNumber);
        result = submitOrderRequest(request);

        Thread.sleep(1100L);

        // 使用 driver 打开!
        driver.get("http://localhost" + result);
        PaySuccessPage.waitingForSuccess(this, driver, 3);
        quickDoneForAuthorising(authorising);

        // 再试一次？ 肯定是不行的
        result = submitOrderRequest(request);
        assertThat(result)
                .contains("InvalidAuthorisingException");

        // 持续等待……
//        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 将这个按揭码相关的订单立刻完成掉
     *
     * @param authorising 按揭码
     */
    private void quickDoneForAuthorising(String authorising) throws Exception {
        // 查询该支付订单
        MainOrder order = null;
        while (order == null) {
            order = trjService.findOrder(authorising);
            Thread.sleep(100);
        }


        // 让root干活了
        Login login = allRunWith();
        try {
            updateAllRunWith(newRandomManager(ManageLevel.root));
            mockMvc.perform(post("/orderData/quickDone/" + order.getId())
                    .param("deliverCompany", RandomStringUtils.randomAlphabetic(10))
                    .param("deliverStore", RandomStringUtils.randomAlphabetic(10))
                    .param("stockQuantity", String.valueOf(1 + random.nextInt(100)))
                    .param("shipmentTime", LocalDate.now().format(dateFormatter))
                    .param("deliverTime", LocalDate.now().format(dateFormatter))
            )
                    .andExpect(status().is2xxSuccessful());
        } finally {
            updateAllRunWith(login);
        }
    }


}
