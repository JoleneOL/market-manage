package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.model.OrderRequest;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.ChannelService;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import cn.lmjia.market.core.trj.TRJService;
import cn.lmjia.market.manage.config.ManageConfig;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import cn.lmjia.market.wechat.page.WechatOrderPage;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 针对投融家的订单测试
 *
 * @author CJ
 */
@ContextConfiguration(classes = {SecurityConfig.class, ManageConfig.class})
public class WechatMainOrderControllerTest2 extends WechatTestBase {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private TRJService trjService;
    @Autowired
    private ReadService readService;
    @Autowired
    private ChannelService channelService;

    @Test
    public void go() throws Exception {
        //选择一个商品的价格 认定它为投融家价格
        Channel trj = channelService.findByName(TRJService.ChannelName);

        final MainGood good = mainGoodService.forSale(trj).get(0);

        BigDecimal price = good.getTotalPrice();

        final Login login = randomLogin(false);
        updateAllRunWith(login);
        mockMvc.perform(wechatGet(TRJEnhanceConfig.TRJOrderURI))
                .andExpect(status().isOk())
                .andExpect(view().name("wechat@orderPlace.html"));
        // 应该只能看到部分商品
        driver.get("http://localhost" + TRJEnhanceConfig.TRJOrderURI);
        WechatOrderPage orderPage = initPage(WechatOrderPage.class);

        orderPage.allPriced(price);

        // 不提交也不行
        OrderRequest request = randomOrderRequest(trj.getId(), good, null, null);
        String result = submitOrderRequest(request);
        // 应该是一个错误地址
        assertThat(result)
                .contains("InvalidAuthorisingException");
        // 特定按揭码和身份证给他们
        request = randomOrderRequest(trj.getId(), good, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomNumeric(18));
        result = submitOrderRequest(request);
        // 应该是一个错误地址
        assertThat(result)
                .contains("InvalidAuthorisingException");
        // 新增按揭码
        final String authorising = RandomStringUtils.randomAlphabetic(10);
        final String idNumber = RandomStringUtils.randomNumeric(18);
        addAuthorising(authorising, idNumber);
        // 使用刚新增的按揭码
        request = randomOrderRequest(trj.getId(), good, authorising, idNumber);
        result = submitOrderRequest(request);

        Thread.sleep(1100L);

        // 使用 driver 打开!
        driver.get("http://localhost" + result);
        PaySuccessPage.waitingForSuccess(this, driver, 3);

        // 添加一个客服好让它收到消息
        addCustomerServiceWithDeveloperWechatId();

        quickDoneForAuthorising(authorising);

        assertThat(readService.currentBalance(login).getAmount())
                .isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ONE));

        // 管理员是否可以看到？
        checkManageMortgageTRGFor(authorising);
        // 让管理员发起完成申请
        makeRequest(authorising);
        // 测试信审通过
        makeAuthorisingCheck(authorising, true);
        assertThat(readService.currentBalance(login).getAmount())
                .isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ONE));
        // 测试结算通过
        makeAuthorisingSettlement(authorising);
        assertThat(readService.currentBalance(login).getAmount())
                .isGreaterThan(BigDecimal.ZERO);

        // 再试一次？ 肯定是不行的
        result = submitOrderRequest(request);
        assertThat(result)
                .contains("InvalidAuthorisingException");

        // 持续等待……
//        Thread.sleep(Long.MAX_VALUE);
    }

    private void makeAuthorisingSettlement(String authorising) throws Exception {
        MainOrder order = trjService.findOrder(authorising);
        Login current = allRunWith();
        try {
            mockMvc.perform(post("/_tourongjia_event_")
                    .param("event", "v4")
                    .param("authorising", authorising)
                    .param("orderId", String.valueOf(order.getId()))
                    .param("time", LocalDateTime.now().format(dateTimeFormatter))
            )
                    .andExpect(status().isOk())
                    .andExpect(similarJsonObjectAs("classpath:/mock/trj_response.json"));
        } finally {
            updateAllRunWith(current);
        }
    }

    private void makeAuthorisingCheck(String authorising, boolean result) throws Exception {
        MainOrder order = trjService.findOrder(authorising);
        Login current = allRunWith();
        try {
            mockMvc.perform(post("/_tourongjia_event_")
                    .param("event", "v1")
                    .param("authorising", authorising)
                    .param("orderId", String.valueOf(order.getId()))
                    .param("message", RandomStringUtils.randomAlphabetic(20))
                    .param("result", String.valueOf(result))
            )
                    .andExpect(status().isOk())
                    .andExpect(similarJsonObjectAs("classpath:/mock/trj_response.json"));
        } finally {
            updateAllRunWith(current);
        }
    }

    private void makeRequest(String authorising) throws Exception {
        MainOrder order = trjService.findOrder(authorising);
        // mortgageTRGAppeal
        Login login = allRunWith();
        try {
            updateAllRunWith(newRandomManager(ManageLevel.root));

            mockMvc.perform(get("/mortgageTRGAppeal?id=" + order.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("_appeal.html"));
            // 提交申请

            mockMvc.perform(post("/mortgageTRGAppeal")
                    .param("id", String.valueOf(order.getId()))
                    .param("installer", RandomStringUtils.randomAlphabetic(10))
                    .param("installCompany", RandomStringUtils.randomAlphabetic(20))
                    .param("mobile", randomMobile())
                    .param("installDate", LocalDate.now().format(dateFormatter))
                    .param("applyFile", newRandomImagePath())
            )
                    .andExpect(status().is3xxRedirection());

        } finally {
            updateAllRunWith(login);
        }
    }

    private void checkManageMortgageTRGFor(String authorising) throws Exception {
        Login login = allRunWith();
        try {
            updateAllRunWith(newRandomManager(ManageLevel.root));

            mockMvc.perform(get("/mortgageTRG"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("_mortgageTRG.html"));
            // 获取数据
            mockMvc.perform(get("/manage/mortgage").param("mortgageCode", authorising))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(jsonPath("$.data.length()").value(1));

        } finally {
            updateAllRunWith(login);
        }
    }

    private void addCustomerServiceWithDeveloperWechatId() {
        Manager manager = newRandomManager(ManageLevel.customerService);
        bindDeveloperWechat(manager);
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
